package com.initial1ze.loggr.service;

import com.initial1ze.loggr.enitity.LogEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class LogBuffer {

    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>(10000);

    public void add(LogEntry logEntry) throws InterruptedException {
        queue.put(logEntry);
    }

    public List<LogEntry> drain(int maxItems) {
        List<LogEntry> drained = new ArrayList<>();
        queue.drainTo(drained, maxItems);
        return drained;
    }

    public int size() {
        return queue.size();
    }
}
