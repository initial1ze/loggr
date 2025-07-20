package com.initial1ze.loggr.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class LogSearchRequestDTO {
    private String resourceId;
    private String level;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant end;
}
