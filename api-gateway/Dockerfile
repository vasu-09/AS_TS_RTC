# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# 1) Cache deps
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -q -DskipTests dependency:go-offline

# 2) Build jar
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -q -DskipTests package

# 3) Copy the bootable jar to a fixed name (no shell needed later)
RUN JAR="$(ls target/*-SNAPSHOT.jar 2>/dev/null || ls target/*jar | head -n1)" \
 && mkdir -p /out \
 && cp "$JAR" /out/app.jar

# ---------- Runtime stage ----------
# Distroless = no shell, non-root by default
FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app

# sensible JVM defaults for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+ExitOnOutOfMemoryError"

# bring in the app
COPY --from=build /out/app.jar /app/app.jar

# document the port (K8s Service/Ingress will expose)
# EXPOSE 8080

# run as non-root; distroless already is non-root
ENTRYPOINT ["java","-jar","/app/app.jar"]
