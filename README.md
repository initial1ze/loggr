## Loggr

**Loggr** is a high-performance log ingestion system built using Spring Boot, designed to handle **large volumes of structured logs** efficiently and reliably. It supports multiple input sources, intelligently buffers incoming data, and stores logs persistently in MongoDB using optimized batching.

### Key Capabilities

- **Scalable ingestion** via:
  - RESTful HTTP endpoint
  - Apache Kafka consumer (ideal for asynchronous, high-throughput pipelines)

- **In-memory buffering**:
  - Incoming logs are temporarily held in memory
  - Helps absorb traffic spikes and avoid overwhelming the database

- **Batch processing engine**:
  - Groups logs into batches based on a configured `MAX_BATCH_SIZE`
  - Flushes logs to the database either when the batch is full or after a `MAX_WAIT_TIME_MS` interval
  - Reduces writing overhead and increases throughput by performing fewer I/O operations

- **MongoDB persistence**:
  - Log entries are stored in a flexible schema within a MongoDB collection
  - Queries are optimized with the appropriate indexing for efficient retrieval

This architecture ensures that Loggr can **scale horizontally**, maintain **high throughput under load**, and support both **real-time** and **delayed** log ingestion patterns.


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

| Parameter    | Type                  | Description                                                               |
|--------------|-----------------------|---------------------------------------------------------------------------|
| `resourceId` | `string`              | (Optional) Filter by resource ID                                          |
| `level`      | `string`              | (Optional) Filter by log level (e.g., ERROR, INFO)                        |
| `start`      | `datetime` (ISO 8601) | (Optional) Start of time range                                            |
| `end`        | `datetime` (ISO 8601) | (Optional) End of time range                                              |
| `page`       | `int`                 | (Optional) Page number (default = 0)                                      |
| `size`       | `int`                 | (Optional) Page size (default = 20)                                       |
| `sort`       | `string`              | (Optional) Sort field and direction, e.g. `timestamp,desc` or `level,asc` |

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

## Metrics & Monitoring

This application exposes internal metrics using [Micrometer](https://micrometer.io/) and integrates with Prometheus and Grafana for real-time monitoring of log ingestion and system health.

### Exposed Metrics

Metrics are available at the `/actuator/prometheus` endpoint. Key metrics include:

- `loggr_logs_ingested`: Custom counter for number of logs ingested
- JVM metrics (memory usage, threads, GC)
- HTTP request metrics
- Kafka consumer metrics

---

## Prometheus & Grafana Setup

Run `docker-compose.yml` to run Prometheus and Grafana in docker.

### Viewing Metrics in Grafana

Visit [http://localhost:3000](http://localhost:3000) (default Grafana port)

**Login with default credentials:**

- Username: `admin`
- Password: `admin`

**Steps:**

1. Add Prometheus as a data source: `http://prometheus:9090`
2. Create dashboards or import pre-built dashboards for:
   - JVM metrics
   - Kafka monitoring
   - Logs Ingesting Monitoring
   - Spring Boot application metrics
3. Use the `loggr_logs_ingested` metric to visualize the log ingestion rate over time.

---

## TODO

- Add support for exporting logs (e.g., to CSV)
- Implement indexing on MongoDB fields for better query performance
- Integrate with frontend dashboard for visualizing logs
