package com.example.CashRegisterHandler.kafka;

import com.example.CashRegisterHandler.dto.ExchangeValuesDTO;
import com.example.CashRegisterHandler.dto.ExchangedCurrencyDTO;
import com.example.CashRegisterHandler.kafka.producer.KafkaProducerSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class KafkaService {

    @Autowired
    private KafkaProducerSender kafkaProducerSender;

    public void exchangeCurrency(@Validated ExchangeValuesDTO exchangeValuesDTO, String key, String topic) {
        
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
                    exchangeValuesDTO, targetCurrencyCount, targetStoredCurrency
            );
            kafkaProducerSender.sendMessage(exchangedCurrencyDTO, topic, key);
        }
    }

    private ExchangedCurrencyDTO getExchangedCurrencyDTO(ExchangeValuesDTO exchangeValuesDTO, BigDecimal targetCurrencyCount, BigDecimal targetStoredCurrency) {
        ExchangedCurrencyDTO exchangedCurrencyDTO = new ExchangedCurrencyDTO();
        //Считаем остаток в кассе валюты, из которой переводит пользователь
        exchangedCurrencyDTO.setBaseStoredCurrency(
                exchangeValuesDTO.getBaseStoredCurrency()
                        .add(exchangeValuesDTO.getBaseCurrencyCount())
                        .setScale(2, RoundingMode.HALF_UP)
        );
        exchangedCurrencyDTO.setTargetCurrencyCount(targetCurrencyCount);
        exchangedCurrencyDTO.setTargetStoredCurrency(targetStoredCurrency);
        return exchangedCurrencyDTO;
    }
}
