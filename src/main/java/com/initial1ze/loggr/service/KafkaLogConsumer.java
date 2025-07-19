package com.initial1ze.loggr.service;

import com.initial1ze.loggr.enitity.LogEntry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogConsumer {

    private final LogBuffer logBuffer;

    public KafkaLogConsumer(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @KafkaListener(topics = "logs-topic", groupId = "log-ingestion-group", containerFactory = "logKafkaListenerContainerFactory")
    public void consumeLog(LogEntry logEntry) throws InterruptedException {
        logBuffer.add(logEntry);
        System.out.println("Consumed from Kafka: " + logEntry);
    }
}

