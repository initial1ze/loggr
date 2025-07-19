package com.initial1ze.loggr.enitity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "logEntry")
public class LogEntry {
    @Id
    private String traceId;
    private String level;
    private String message;
    private String resourceId;
    private Instant timestamp;
    private String spanId;
    private String commit;
    private Metadata metadata;
}
