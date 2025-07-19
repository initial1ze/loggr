package com.initial1ze.loggr.repository;

import com.initial1ze.loggr.enitity.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoggrRepository extends MongoRepository<LogEntry, String> {
}
