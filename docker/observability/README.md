# Observability Stack

Local Docker flow:

```text
Spring Boot server
  | OTLP HTTP/gRPC
  v
OpenTelemetry Collector
  | metrics        | logs          | traces
  v                v               v
Prometheus       Loki            Tempo
  \                |               /
   \               |              /
    ------------ Grafana --------

Docker stdout -> Logstash -> Elasticsearch -> Kibana
```

Endpoints:

- Application: http://localhost:8080
- OpenTelemetry Collector OTLP gRPC: `localhost:4317`
- OpenTelemetry Collector OTLP HTTP: `localhost:4318`
- Collector Prometheus metrics endpoint: http://localhost:9464/metrics
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Loki: http://localhost:3100
- Tempo: http://localhost:3200
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- Logstash GELF UDP input: `localhost:12201`

Grafana login is read from Docker Compose environment variables:

- Username: `GRAFANA_ADMIN_USER`
- Password: `GRAFANA_ADMIN_PASSWORD`

Start the full local stack from the repository root:

```bash
cp .env.example .env
# Fill GRAFANA_ADMIN_PASSWORD and the other required secrets first.
docker compose up --build
```

This stack is configured for local development. Elasticsearch security is disabled in Docker Compose so the services can
start without certificates or passwords. Enable security before using this architecture outside a local environment.
