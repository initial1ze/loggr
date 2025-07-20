package com.initial1ze.loggr.service;

import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.repository.LoggrRepository;
import com.initial1ze.loggr.spring.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class LogProcessor {
    private final Logger LOG = LoggerFactory.getLogger(LogProcessor.class);

    private final LogBuffer logBuffer;
    private final LoggrRepository loggrRepository;
    private final MetricsService metricsService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private long lastFlushTime = System.currentTimeMillis();

    private final Integer MAX_BATCH_SIZE;
    private final Long MAX_WAIT_TIME_MS;

    public LogProcessor(LogBuffer logBuffer, LoggrRepository loggrRepository, MetricsService metricsService,
                        @Value("${loggr.bufferSize}") Integer maxBatchSize, @Value("${loggr.flushDelay}") Long maxWaitTimeMs) {
        this.logBuffer = logBuffer;
        this.loggrRepository = loggrRepository;
        this.metricsService = metricsService;
        this.MAX_BATCH_SIZE = maxBatchSize;
        this.MAX_WAIT_TIME_MS = maxWaitTimeMs;
        startProcessing();
    }

    private void startProcessing() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                int queueSize = logBuffer.size();
                long now = System.currentTimeMillis();

                boolean flushDueToSize = queueSize >= MAX_BATCH_SIZE;
                boolean flushDueToTime = queueSize > 0 && (now - lastFlushTime >= MAX_WAIT_TIME_MS);
                if (flushDueToSize || flushDueToTime) {
                    List<LogEntry> batch = logBuffer.drain(MAX_BATCH_SIZE);
                    if (!batch.isEmpty()) {
                        process(batch);
                        lastFlushTime = now;
                    }
                }
            } catch (Exception e) {
                LOG.error("Error during batch processing: {}", e.getMessage(), e);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void process(List<LogEntry> batch) {
        try {
            loggrRepository.saveAll(batch);
            metricsService.incrementBy(batch.size());
            LOG.info("Saved batch of size: {}", batch.size());
        } catch (Exception e) {
            LOG.error("Failed to save logs: {}", e.getMessage(), e);
        }
    }
}
