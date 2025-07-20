package com.initial1ze.loggr.controller;

import com.initial1ze.loggr.dto.LogEntryDTO;
import com.initial1ze.loggr.dto.LogSearchRequestDTO;
import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.service.LoggrService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@Slf4j
public class LoggrController {
    private final Logger LOG = LoggerFactory.getLogger(LoggrController.class);

    private final LoggrService loggrService;

    public LoggrController(final LoggrService loggrService) {
        this.loggrService = loggrService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestLog(@RequestBody LogEntryDTO logEntryDTO) throws InterruptedException {
        LOG.info("Received ingest request for logEntry: {}", logEntryDTO);
        LogEntry logEntry = LogEntryDTO.toLogEntry(logEntryDTO);
        loggrService.addToBuffer(logEntry);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/count")
    public ResponseEntity<?> logsCount() {
        LOG.info("Received logs total count request");
        return ResponseEntity.ok(Map.of("count", loggrService.getCount()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLogs(
            LogSearchRequestDTO request, Pageable pageable, @RequestParam(required = false) List<String> sort
    ) {
        LOG.info("Received search logs request with parameters: request: {}, pageable: {}", request, pageable);
        Page<LogEntry> logs = loggrService.searchLogs(request, pageable, sort);
        return ResponseEntity.ok(logs);
    }
}
