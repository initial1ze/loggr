package com.initial1ze.loggr.service;

import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.repository.LoggrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggrService {
    private final LogBuffer logBuffer;
    private final LoggrRepository loggrRepository;

    public LoggrService(final LoggrRepository loggrRepository, final LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
        this.loggrRepository = loggrRepository;
    }

    public long getCount() {
        return loggrRepository.count();
    }

    public void addToBuffer(LogEntry logEntry) throws InterruptedException {
        logBuffer.add(logEntry);
    }
}
