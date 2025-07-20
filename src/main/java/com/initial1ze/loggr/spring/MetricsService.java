package com.initial1ze.loggr.spring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsService {
    private final Counter logCounter;

    public MetricsService(MeterRegistry registry) {
        this.logCounter = Counter.builder("loggr_logs_ingested")
                .description("Number of logs ingested")
                .register(registry);
    }

    public void incrementBy(long count) {
        logCounter.increment(count);
    }
}
