FROM openjdk:21-rc-jdk-slim
WORKDIR /app
COPY /target/veholder-app-0.0.1-SNAPSHOT.jar /app/veholder.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "veholder.jar"]
