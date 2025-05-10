package com.example.CashRegisterHandler.kafka.consumer;

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
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @KafkaListener(topics = "handle-exchange", groupId = "group1")
    void listenerRequiredCurrencyRate(ConsumerRecord<String, String> record) {
        log.info("Received message [{}] in group1", record.value());
        System.out.println(record.value());
    }
}
