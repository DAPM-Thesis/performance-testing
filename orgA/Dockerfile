FROM openjdk:21-slim

WORKDIR /app

# Copy the JAR file and required resources
COPY target/orgA-0.0.1-SNAPSHOT.jar /app/app.jar
COPY src/main/representations /app/orgA/src/main/representations
COPY src/main/config_schemas /app/orgA/src/main/config_schemas

# Expose the application port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]