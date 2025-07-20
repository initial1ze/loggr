package com.initial1ze.loggr.service;

import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.repository.LoggrRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoggrService {
    private final LogBuffer logBuffer;
    private final LoggrRepository loggrRepository;
    private final MongoTemplate mongoTemplate;

    public LoggrService(final LoggrRepository loggrRepository, final LogBuffer logBuffer, MongoTemplate mongoTemplate) {
        this.logBuffer = logBuffer;
        this.loggrRepository = loggrRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public long getCount() {
        return loggrRepository.count();
    }

    public void addToBuffer(LogEntry logEntry) throws InterruptedException {
        logBuffer.add(logEntry);
    }

    public Page<LogEntry> searchLogs(String resourceId, String level, Instant start, Instant end, int page,
                                     int size,
                                     String[] sortParams) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (resourceId != null && !resourceId.isEmpty()) {
            criteriaList.add(Criteria.where("resourceId").is(resourceId));
        }

        if (level != null && !level.isEmpty()) {
            criteriaList.add(Criteria.where("level").is(level));
        }

        criteriaList.add(Criteria.where("timestamp").gte(start.toString()).lte(end.toString()));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);

        for (String param : sortParams) {
            String[] parts = param.split(",");
            if (parts.length == 2) {
                query.with(Sort.by(Sort.Direction.fromString(parts[1]), parts[0]));
            } else {
                query.with(Sort.by(Sort.Direction.ASC, parts[0]));
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        query.with(pageable);

        List<LogEntry> logs = mongoTemplate.find(query, LogEntry.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), LogEntry.class);

        return new PageImpl<>(logs, pageable, total);
    }
}
