package com.initial1ze.loggr.repository;

import com.initial1ze.loggr.enitity.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface LoggrRepository extends MongoRepository<LogEntry, String> {
    List<LogEntry> findByResourceIdContainingIgnoreCaseAndLevelContainingIgnoreCaseAndTimestampBetween(
            String resourceId, String level, Instant start, Instant end
    );
}
