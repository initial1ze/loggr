package com.initial1ze.loggr.enitity;


import com.initial1ze.loggr.dto.LogEntryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collation = "logs")
public class LogEntry {
    @Id
    private String traceId;
    private String level;
    private String message;
    private String resourceId;
    private String timestamp;
    private String spanId;
    private String commit;
    private Metadata metadata;
}
