FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG OTEL_JAVA_AGENT_VERSION=2.27.0
RUN apk add --no-cache ca-certificates wget \
    && mkdir -p /otel \
    && wget -q -O /otel/opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar"
COPY --from=builder /app/target/clothing_shop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080 9091
ENTRYPOINT ["java","-jar","app.jar"]
