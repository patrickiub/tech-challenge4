####
# Multi-stage Dockerfile for Quarkus JVM mode
# Optimized for Google Cloud Run deployment
####

# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

# Copy Maven wrapper and pom first for layer caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Warm up dependency cache
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B || true

# Copy source and build
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

WORKDIR /deployments

# Copy the Quarkus fast-jar output layers
COPY --from=build /workspace/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /workspace/target/quarkus-app/*.jar /deployments/
COPY --from=build /workspace/target/quarkus-app/app/ /deployments/app/
COPY --from=build /workspace/target/quarkus-app/quarkus/ /deployments/quarkus/

# Cloud Run expects the container to listen on $PORT (default 8080)
EXPOSE 8080

# Non-root user for security
RUN useradd -r -u 1001 -g root quarkus && chown -R 1001:0 /deployments
USER 1001

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
