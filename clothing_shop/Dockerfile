# syntax=docker/dockerfile:1.7

ARG MAVEN_IMAGE=maven:3.9.16-eclipse-temurin-25
ARG RUNTIME_IMAGE=eclipse-temurin:25-jre-alpine
ARG OTEL_JAVA_AGENT_VERSION=2.27.0

# --------------------------------------------------------------------------
# Dependencies: cached as long as pom.xml is unchanged, so day-to-day source
# edits never re-download the Maven repository.
# --------------------------------------------------------------------------
FROM ${MAVEN_IMAGE} AS deps
WORKDIR /build
COPY pom.xml .
RUN mvn -B -ntp -DskipTests dependency:go-offline

# --------------------------------------------------------------------------
# Build: inherits the warm ~/.m2 from the deps layer.
# --------------------------------------------------------------------------
FROM deps AS builder
WORKDIR /build
COPY src ./src
# maven.test.skip also skips test compilation; CI owns running the suite.
RUN mvn -B -ntp -Dmaven.test.skip=true -Djacoco.skip=true clean package

# Split the fat jar into lib/ (stable) + thin app.jar (changes every build)
# so image pushes and pulls only move the application layer.
RUN java -Djarmode=tools -jar target/clothing_shop-0.0.1-SNAPSHOT.jar \
        extract --destination /build/extracted \
    && mv /build/extracted/clothing_shop-0.0.1-SNAPSHOT.jar /build/extracted/app.jar

# --------------------------------------------------------------------------
# OpenTelemetry agent: fetched in its own stage so curl and its CA tooling
# never reach the runtime image.
#
# Note: a jlink'd runtime would shrink the 306 MB JRE base considerably, but no
# current musl JDK image (temurin, liberica, zulu alpine) ships the jmods that
# jlink needs, so the full JRE base stays.
# --------------------------------------------------------------------------
FROM ${RUNTIME_IMAGE} AS otel
ARG OTEL_JAVA_AGENT_VERSION
RUN apk add --no-cache curl \
    && curl -fsSL -o /opentelemetry-javaagent.jar \
        "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar"

# --------------------------------------------------------------------------
# Runtime
# --------------------------------------------------------------------------
FROM ${RUNTIME_IMAGE} AS runtime

LABEL org.opencontainers.image.title="vti-clothing-shop-server" \
      org.opencontainers.image.source="https://github.com/vti/clothing-shop" \
      org.opencontainers.image.licenses="Apache-2.0"

RUN addgroup -S spring && adduser -S -H -G spring spring

WORKDIR /app

COPY --from=otel /opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
# Order matters: dependencies rarely change, application code changes every build.
COPY --from=builder --chown=spring:spring /build/extracted/lib ./lib
COPY --from=builder --chown=spring:spring /build/extracted/app.jar ./app.jar

USER spring:spring

EXPOSE 8080 9091

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD wget -q -O- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# Container-aware heap sizing; fail fast instead of thrashing on OOM. Kept in the
# entrypoint rather than JAVA_TOOL_OPTIONS, which compose overrides for the otel agent.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
