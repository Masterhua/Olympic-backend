FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Olympic-app-0.0.1-SNAPSHOT.jar /app/Olympic-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Olympic-app.jar"]
