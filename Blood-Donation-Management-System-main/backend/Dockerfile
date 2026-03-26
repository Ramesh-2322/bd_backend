# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only build descriptor first to maximize layer caching.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

# Copy source and build the application.
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Run as non-root for better security.
RUN useradd --system --uid 1001 --create-home appuser

COPY --from=builder /app/target ./target

ENV PORT=8080
ENV JAVA_OPTS=""

EXPOSE 8080

USER appuser
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar target/*.jar"]
