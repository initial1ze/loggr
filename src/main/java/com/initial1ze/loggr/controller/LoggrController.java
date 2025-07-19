package com.initial1ze.loggr.controller;

import com.initial1ze.loggr.dto.LogEntryDTO;
import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.service.LogBuffer;
import com.initial1ze.loggr.service.LoggrService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<?> logsCount() {
        return new ResponseEntity<>(Map.of("count", loggrService.getCount()), HttpStatus.OK);
    }
}
