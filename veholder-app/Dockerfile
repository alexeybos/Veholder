FROM openjdk:21-rc-jdk-slim
# Устанавливаем необходимые пакеты
RUN apt-get update && \
    apt-get install -y procps && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY /target/veholder-app-0.0.1-SNAPSHOT.jar /app/veholder.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "veholder.jar"]
