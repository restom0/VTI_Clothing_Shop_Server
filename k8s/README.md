# Kubernetes deployment

This folder splits the deployment into two Kustomize modules:

- `monitoring`: OpenTelemetry Collector, Prometheus, Grafana, Loki, Tempo, Elasticsearch, Logstash, and Kibana.
- `package`: Nginx API Gateway, Spring Boot API, PostgreSQL, Valkey, MongoDB, and database init data.

## Build the application image

Build the server image before applying the `package` module to a local cluster:

```powershell
docker build -t vti-clothing-shop-server:latest .\clothing_shop
```

If the cluster cannot read local Docker images directly, push the image to a registry and update
`k8s/package/server.yaml`.

## Apply modules

Create local secret env files first. These files are ignored by Git:

```powershell
Copy-Item .\k8s\monitoring\grafana-secret.env.example .\k8s\monitoring\grafana-secret.env
Copy-Item .\k8s\package\app-secret.env.example .\k8s\package\app-secret.env
```

Fill `grafana-secret.env` and `app-secret.env` with real values before applying Kustomize. Then apply monitoring first
so the application can export telemetry as soon as it starts:

```powershell
kubectl apply -k .\k8s\monitoring
kubectl apply -k .\k8s\package
```

## Local access

Use port-forwarding for local development:

```powershell
kubectl -n vti-clothing-shop port-forward svc/clothing-shop-server 8080:8080
kubectl -n vti-clothing-shop port-forward svc/clothing-shop-server 9091:9091
kubectl -n vti-clothing-shop port-forward svc/api-gateway 8088:80
kubectl -n vti-clothing-shop port-forward svc/api-gateway 19090:9090
kubectl -n vti-clothing-shop-monitoring port-forward svc/grafana 3000:3000
kubectl -n vti-clothing-shop-monitoring port-forward svc/prometheus 9090:9090
kubectl -n vti-clothing-shop-monitoring port-forward svc/kibana 5601:5601
```

Grafana uses the credentials generated from `k8s/monitoring/grafana-secret.env`.

## Notes

- `package` writes to PostgreSQL and MongoDB through the application configuration already present in
  `application.properties`.
- Nginx API Gateway proxies HTTP `/api/*` and `/` to `clothing-shop-server:8080`, and proxies gRPC module paths to
  `clothing-shop-server:9091`.
- PostgreSQL bootstrap data is loaded from `docker/postgres/init/01_dummy_data.sql` through a generated ConfigMap.
- Telemetry is sent to `otel-collector.vti-clothing-shop-monitoring.svc.cluster.local:4318`.
- Do not commit `k8s/package/app-secret.env` or `k8s/monitoring/grafana-secret.env`; only the `.example` templates are
  tracked.
