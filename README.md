# Loggr

This is a Spring Boot-based log ingestion server that supports high-throughput log processing via:

- HTTP endpoint for pushing logs
- Kafka consumer for receiving logs from a topic
- In-memory buffering with batch processing
- MongoDB persistence

## Features

- Accepts logs via REST API and Kafka
- Buffers logs in memory to handle spikes
- Flushes logs to MongoDB when:
  - Buffer reaches `MAX_BATCH_SIZE`
  - OR `MAX_WAIT_TIME_MS` has passed since last flush

---

## Requirements

- Java 17+
- Maven 3.8+
- MongoDB (local or remote)
- Apache Kafka

---

## Configuration

Set the following in `application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/loggingdb
spring.data.mongodb.database=loggingdb

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=log-ingestion-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
```

---

## Build & Run

### Build with Maven:

```bash
mvn clean install
```

### Run the application:

```bash
mvn spring-boot:run
```

---

## HTTP API

### POST `/ingest`

Ingest a log entry.

#### Request Body (JSON):

```json
{
  "traceId": "abc123",
  "level": "ERROR",
  "message": "Something went wrong",
  "resourceId": "service-A",
  "timestamp": "2023-09-15T08:15:00Z",
  "spanId": "def456",
  "commit": "a1b2c3d",
  "metadata": {
    "env": "prod",
    "host": "host123"
  }
}
```

#### Example with curl:

```bash
curl -X POST http://localhost:8080/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "traceId": "abc123",
    "level": "INFO",
    "message": "Something happened",
    "resourceId": "my-service",
    "timestamp": "2025-07-19T10:20:00Z",
    "spanId": "xyz789",
    "commit": "a1b2c3d",
    "metadata": {
      "env": "production",
      "region": "us-east-1"
    }
  }'

```

---

## Kafka Integration

Logs sent to the Kafka topic `logs-topic` will be deserialized into `LogEntry` objects and buffered for MongoDB ingestion.

Make sure the messages match the `LogEntry` structure:
```json
{
  "traceId": "string",
  "level": "string",
  "message": "string",
  "resourceId": "string",
  "timestamp": "ISO 8601 UTC",
  "spanId": "string",
  "commit": "string",
  "metadata": {
    // Custom metadata specific to your application or environment
  }
}
```

---

## MongoDB Schema

Each log entry is stored as a document in the `logs` collection:

```json
{
  "traceId": "string",
  "level": "string",
  "message": "string",
  "resourceId": "string",
  "timestamp": "ISO 8601 UTC",
  "spanId": "string",
  "commit": "string",
  "metadata": {
    // Custom metadata specific to your application or environment
  }
}
```

---

## Customization

You can tune batching behavior via constants in `LogProcessor` from `application.properties`.


## TODO

- Implement log search by text, source, and time range
- Add pagination and sorting to log retrieval API
- Create endpoints for filtering logs by severity level
- Add support for exporting logs (e.g., to CSV)
- Implement indexing on MongoDB fields for better query performance
- Integrate with frontend dashboard for visualizing logs
