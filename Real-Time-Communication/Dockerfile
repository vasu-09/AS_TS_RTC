# ---- build stage ----
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -DskipTests clean package

# ---- runtime stage ----
FROM eclipse-temurin:21-jre
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"
RUN useradd -ms /bin/bash spring
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
USER spring
EXPOSE 8091
ENTRYPOINT ["java","-jar","/app/app.jar"]
