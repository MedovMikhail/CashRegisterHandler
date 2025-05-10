package com.example.CashRegisterHandler.kafka.consumer;

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
    void listenerRequiredCurrencyRate(ConsumerRecord<String, String> record) {
        log.info("Received message [{}] in group1", record.value());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExchangeValuesDTO exchangeValuesDTO = objectMapper.readValue(record.value(), ExchangeValuesDTO.class);
            kafkaService.exchangeCurrency(exchangeValuesDTO, record.key(), "exchanged-currency");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
