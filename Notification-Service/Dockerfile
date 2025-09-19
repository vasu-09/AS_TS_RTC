# ---- Build ----
FROM maven:3.9.8-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Cache dependencies first (faster rebuilds)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
# Now add sources and build
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---- Runtime ----
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
# Create non-root with stable UID (good for K8s)
RUN adduser -D -u 1001 -s /sbin/nologin spring
USER 1001
COPY --from=build /app/target/*.jar /app/app.jar

# Adjust if your service runs on another port
EXPOSE 8093

# Helpful default memory setting for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java","-jar","/app/app.jar"]
