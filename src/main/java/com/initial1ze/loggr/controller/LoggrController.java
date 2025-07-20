package com.initial1ze.loggr.controller;

import com.initial1ze.loggr.dto.LogEntryDTO;
import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.service.LoggrService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class LoggrController {

    private final LoggrService loggrService;

    public LoggrController(final LoggrService loggrService) {
        this.loggrService = loggrService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestLog(@RequestBody LogEntryDTO logEntryDTO) throws InterruptedException {
        LogEntry logEntry = LogEntryDTO.toLogEntry(logEntryDTO);
        loggrService.addToBuffer(logEntry);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/count")
    public ResponseEntity<?> logsCount() {
        return ResponseEntity.ok(Map.of("count", loggrService.getCount()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLogs(
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp,desc") String[] sort
    ) {
        start = start != null ? start : Instant.EPOCH;
        end = end != null ? end : Instant.now();
        Page<LogEntry> logs = loggrService.searchLogs(resourceId, level, start, end, page, size, sort);
        return ResponseEntity.ok(logs);
    }
}
