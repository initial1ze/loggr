package com.initial1ze.loggr.spring;

import com.initial1ze.loggr.repository.LoggrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupHealthChecker implements CommandLineRunner {
    private final Logger LOG = LoggerFactory.getLogger(StartupHealthChecker.class);

    private final LoggrRepository loggrRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void run(String... args) {
        checkMongoConnection();
        checkKafkaConnection();
    }

    private void checkMongoConnection() {
        try {
            loggrRepository.count();
            LOG.info("Successfully connected to the MongoDB.");
        } catch (Exception e) {
            LOG.error("MongoDB connection FAILED", e);
            throw new IllegalStateException("MongoDB is not available", e);
        }
    }

    private void checkKafkaConnection() {
        try {
            kafkaTemplate.send("health-check-topic", "ping").get(5, TimeUnit.SECONDS);
            LOG.info("Successfully connected to the Kafka.");
        } catch (Exception e) {
            LOG.error("Kafka connection FAILED", e);
            throw new IllegalStateException("Kafka is not available during startup", e);
        }
    }
}

