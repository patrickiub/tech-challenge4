####
# Multi-stage Dockerfile for Quarkus JVM mode
# Optimized for Google Cloud Run deployment via Cloud Build
####

# Build stage - use official Maven image (Cloud Build has no BuildKit)
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy pom first to leverage Docker layer cache for dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src src
RUN mvn package -DskipTests -B

# Runtime stage - JRE only, smaller image
FROM eclipse-temurin:17-jre

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

WORKDIR /deployments

# Copy the Quarkus fast-jar output
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
