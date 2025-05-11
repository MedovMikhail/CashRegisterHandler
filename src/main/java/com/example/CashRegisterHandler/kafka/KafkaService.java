package com.example.CashRegisterHandler.kafka;

import com.example.CashRegisterHandler.dto.CurrencyRecountDTO;
import com.example.CashRegisterHandler.dto.ExchangeValuesDTO;
import com.example.CashRegisterHandler.dto.ExchangedCurrencyDTO;
import com.example.CashRegisterHandler.dto.StoredCurrencyDTO;
import com.example.CashRegisterHandler.kafka.producer.KafkaProducerSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class KafkaService {

    @Autowired
    private KafkaProducerSender kafkaProducerSender;

    public void exchangeCurrency(@Validated ExchangeValuesDTO exchangeValuesDTO, String topic, String key) {
        
        //Считаем количество валюты, в которую хочет перевести пользователь
        BigDecimal targetCurrencyCount = exchangeValuesDTO
                .getBaseCurrencyCount()
                .multiply(exchangeValuesDTO.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);
        
        //Считаем остаток в кассе валюты, в которую переводит пользователь
        BigDecimal targetStoredCurrency = exchangeValuesDTO
                .getTargetStoredCurrency()
                .subtract(targetCurrencyCount)
                .setScale(2, RoundingMode.HALF_UP);
        if (targetStoredCurrency.compareTo(BigDecimal.ZERO) < 0) {
            kafkaProducerSender.sendMessage("Not enough cash", topic, key);
        } else {
            ExchangedCurrencyDTO exchangedCurrencyDTO = getExchangedCurrencyDTO(
                    exchangeValuesDTO, targetCurrencyCount
            );
            kafkaProducerSender.sendMessage(exchangedCurrencyDTO, topic, key);
        }
    }

    public void recountCurrency(CurrencyRecountDTO recountDTO, String topic, String key) {
        HashMap<String, BigDecimal> currencyRates = recountDTO.getCurrencyRates();
        for (StoredCurrencyDTO storedCurrency: recountDTO.getStoredCurrencies()) {
            // актуальный курс валюты
            BigDecimal newExchangeRate = currencyRates.get(storedCurrency.getCurrencyCode());
            // получаем соотношение актуального курса к старому
            BigDecimal exchangeCurrencyScale = newExchangeRate.divide(
                    storedCurrency.getExchangeRate(),
                    8,
                    RoundingMode.HALF_UP
            );
            // Умножаем количество валюты на соотношение курсов
            storedCurrency.setCount(
                    storedCurrency.getCount().multiply(exchangeCurrencyScale).setScale(2, RoundingMode.HALF_UP)
            );
            // задаем актуальный курс валюты
            storedCurrency.setExchangeRate(newExchangeRate);
        }
        kafkaProducerSender.sendMessage(recountDTO.getStoredCurrencies(), topic, key);
    }

    private ExchangedCurrencyDTO getExchangedCurrencyDTO(ExchangeValuesDTO exchangeValuesDTO, BigDecimal targetCurrencyCount) {
        ExchangedCurrencyDTO exchangedCurrencyDTO = new ExchangedCurrencyDTO();
        //Считаем остаток в кассе валюты, из которой переводит пользователь
        exchangedCurrencyDTO.setBaseStoredCurrencyDiff(
                exchangeValuesDTO.getBaseStoredCurrency()
                        .add(exchangeValuesDTO.getBaseCurrencyCount())
                        .setScale(2, RoundingMode.HALF_UP)
        );
        exchangedCurrencyDTO.setExchangeRate(exchangeValuesDTO.getExchangeRate());
        exchangedCurrencyDTO.setTargetStoredCurrencyDiff(targetCurrencyCount);
        exchangedCurrencyDTO.setDateOfExchange(ZonedDateTime.now(ZoneId.of("Europe/Moscow")));
        return exchangedCurrencyDTO;
    }
}
