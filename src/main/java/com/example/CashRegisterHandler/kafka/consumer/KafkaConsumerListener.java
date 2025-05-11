package com.example.CashRegisterHandler.kafka.consumer;

import com.example.CashRegisterHandler.dto.CurrencyRecountDTO;
import com.example.CashRegisterHandler.dto.ExchangeValuesDTO;
import com.example.CashRegisterHandler.kafka.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerListener {

    @Autowired
    private KafkaService kafkaService;

    @KafkaListener(topics = "handle-exchange", groupId = "group1")
    void listenerProcessingCurrencyExchange(ConsumerRecord<String, String> record) {
        log.info("Received message [{}] in group1", record.value());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExchangeValuesDTO exchangeValuesDTO = objectMapper.readValue(record.value(), ExchangeValuesDTO.class);
            kafkaService.exchangeCurrency(exchangeValuesDTO, "exchanged-currency", record.key());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "recount-currency", groupId = "group1")
    void listenerRecountStoredCurrency(ConsumerRecord<String, String> record) {
        log.info("Received message [{}] in group1", record.value());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CurrencyRecountDTO currencyRecountDTO = objectMapper.readValue(record.value(), CurrencyRecountDTO.class);
            kafkaService.recountCurrency(currencyRecountDTO, "get-recount-currency", record.key());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
