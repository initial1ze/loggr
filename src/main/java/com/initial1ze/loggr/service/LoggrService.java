package com.initial1ze.loggr.service;

import com.initial1ze.loggr.dto.LogSearchRequestDTO;
import com.initial1ze.loggr.enitity.LogEntry;
import com.initial1ze.loggr.repository.LoggrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoggrService {
    private final Logger LOG = LoggerFactory.getLogger(LoggrService.class);

    private final LogBuffer logBuffer;
    private final LoggrRepository loggrRepository;
    private final MongoTemplate mongoTemplate;

    public LoggrService(final LoggrRepository loggrRepository, final LogBuffer logBuffer, MongoTemplate mongoTemplate) {
        this.logBuffer = logBuffer;
        this.loggrRepository = loggrRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public long getCount() {
        long totalLogsCount = loggrRepository.count();
        LOG.info("Total logs count in Mongo DB: {}", totalLogsCount);
        return totalLogsCount;
    }

    public void addToBuffer(LogEntry logEntry) throws InterruptedException {
        LOG.info("Adding to the buffer logEntry: {}", logEntry);
        logBuffer.add(logEntry);
    }

    public Page<LogEntry> searchLogs(LogSearchRequestDTO request, Pageable pageable, List<String> sortParams) {
        String resourceId = Optional.ofNullable(request.getResourceId()).orElse("");
        String level = Optional.ofNullable(request.getLevel()).orElse("");
        Instant start = Optional.ofNullable(request.getStart()).orElse(Instant.EPOCH);
        Instant end = Optional.ofNullable(request.getEnd()).orElse(Instant.now());
        List<Criteria> criteriaList = new ArrayList<>();

        if (!resourceId.isEmpty()) {
            criteriaList.add(Criteria.where("resourceId").is(resourceId));
        }

        if (!level.isEmpty()) {
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

        query.with(pageable);

        List<LogEntry> logs = mongoTemplate.find(query, LogEntry.class);
        LOG.info("Received data from MongoDB for search query: {}", logs);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), LogEntry.class);

        return new PageImpl<>(logs, pageable, total);
    }
}
