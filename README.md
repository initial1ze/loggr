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
  "message": "Something failed",
  "resourceId": "service-A",
  "timestamp": "2025-07-19T10:20:00Z",
  "spanId": "def456",
  "commit": "abc789",
  "metadata": {
    "user": "raj",
    "env": "prod"
  }
}
```

#### Example with curl:

```bash
curl -X POST http://localhost:8080/ingest \
  -H "Content-Type: application/json" \
  -d '{"traceId":"abc123","level":"ERROR","message":"Something failed","resourceId":"service-A","timestamp":"2025-07-19T10:20:00Z","spanId":"def456","commit":"abc789","metadata":{"user":"raj","env":"prod"}}'
```

---

### GET `/search`

Search logs with dynamic filters, pagination, and sorting.

#### Query Parameters:

| Parameter     | Type     | Description                                |
|---------------|----------|--------------------------------------------|
| `resourceId`  | `string` | (Optional) Filter by resource ID           |
| `level`       | `string` | (Optional) Filter by log level (e.g., ERROR, INFO) |
| `start`       | `datetime` (ISO 8601) | (Optional) Start of time range |
| `end`         | `datetime` (ISO 8601) | (Optional) End of time range   |
| `page`        | `int`    | (Optional) Page number (default = 0)       |
| `size`        | `int`    | (Optional) Page size (default = 20)        |
| `sort`        | `string` | (Optional) Sort field and direction, e.g. `timestamp,desc` or `level,asc` |

#### Example:

```bash
curl "http://localhost:8080/search?resourceId=service-A&level=ERROR&page=0&size=10&sort=timestamp,desc"
```

#### Example Response:

```json
{
  "content": [
    {
      "traceId": "abc123",
      "level": "ERROR",
      "message": "Something failed",
      "resourceId": "service-A",
      "timestamp": "2025-07-19T10:20:00Z",
      "spanId": "def456",
      "commit": "abc789",
      "metadata": {
        "user": "raj",
        "env": "prod"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

## MongoDB Schema

Each log entry is stored as a document in the `logEntry` collection:

```json
{
  "_id": "auto-generated",
  "traceId": "abc123",
  "level": "ERROR",
  "message": "Something failed",
  "resourceId": "service-A",
  "timestamp": "2025-07-19T10:20:00Z",
  "spanId": "def456",
  "commit": "abc789",
  "metadata": {
    "user": "raj",
    "env": "prod"
  }
}
```

---

## Customization

You can tune batching behavior via constants in `LogProcessor` from `application.properties`.

---

## TODO

- Add support for exporting logs (e.g., to CSV)
- Implement indexing on MongoDB fields for better query performance
- Integrate with frontend dashboard for visualizing logs
