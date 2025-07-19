package com.initial1ze.loggr.dto;

import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.enitity.Metadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEntryDTO {
    private String level;
    private String message;
    private String resourceId;
    private String timestamp;
    private String traceId;
    private String spanId;
    private String commit;
    private MetadataDTO metadata;

    public static LogEntry toLogEntry(LogEntryDTO logEntryDTO) {
        LogEntry logEntry = new LogEntry();
        logEntry.setCommit(logEntryDTO.getCommit());
        logEntry.setLevel(logEntryDTO.getLevel());
        logEntry.setMessage(logEntryDTO.getMessage());
        logEntry.setResourceId(logEntryDTO.getResourceId());
        logEntry.setSpanId(logEntryDTO.getSpanId());
        logEntry.setTimestamp(logEntryDTO.getTimestamp());
        logEntry.setTraceId(logEntryDTO.getTraceId());
        logEntry.setMetadata(new Metadata(logEntryDTO.getResourceId()));
        return logEntry;
    }
}
