#!/bin/bash

# Конфигурация
PROJECT_DIR="/Veholder/veholder-app"
MAVEN_CMD=""
JAVA_VERSION="21"

# Docker параметры
USE_DOCKER=true
DOCKER_IMAGE_NAME="veholder-app_app"
DOCKER_CONTAINER_NAME="veholder-container"
USE_DOCKER=true

LOCAL_JAR_PATH="./target/veholder-app-0.0.1-SNAPSHOT.jar"
CONTAINER_JAR_PATH="/app/veholder.jar"


log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# Функция проверки и настройки Java
setup_java() {
    log "Проверяем установку Java $JAVA_VERSION..."
    
    # Проверяем установленную версию Java
    if command -v java &> /dev/null; then
        CURRENT_JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
        log "Текущая версия Java: $CURRENT_JAVA_VERSION"
        
        if [ "$CURRENT_JAVA_VERSION" != "$JAVA_VERSION" ]; then
            log "Требуется Java $JAVA_VERSION, но установлена $CURRENT_JAVA_VERSION"
            exit 1
        else
            log "Правильная версия Java уже установлена"
        fi
    else
        log "Java не установлена"
        log "Установите Java $JAVA_VERSION:"
        log "   sudo apt update"
        log "   sudo apt install openjdk-$JAVA_VERSION-jdk"
        exit 1
    fi
    
    log "Проверяем установку Java JDK $JAVA_VERSION..."
    
    # Проверяем наличие компилятора javac
    if ! command -v javac &> /dev/null; then
        log "JDK не установлен (отсутствует javac)"
        log "Устанавливаем OpenJDK $JAVA_VERSION JDK..."
        
        # Устанавливаем JDK
        sudo apt update && sudo apt install -y openjdk-$JAVA_VERSION-jdk
        
        if [ $? -eq 0 ]; then
            log "JDK успешно установлен"
            
            # Обновляем переменные среды
            export JAVA_HOME=/usr/lib/jvm/java-$JAVA_VERSION-openjdk-amd64
            export PATH=$JAVA_HOME/bin:$PATH
            
            # Проверяем установку
            log "Проверяем установку JDK..."
            java -version
            javac -version
        else
            log "Не удалось установить JDK"
            exit 1
        fi
    else
        # Проверяем версию javac
        JAVAC_VERSION=$(javac -version 2>&1 | awk '{print $2}' | cut -d'.' -f1)
        log "Версия javac: $JAVAC_VERSION"
        
        if [ "$JAVAC_VERSION" != "$JAVA_VERSION" ]; then
            log "Версия javac ($JAVAC_VERSION) не совпадает с требуемой ($JAVA_VERSION)"
            exit 1
            #log "Устанавливаем правильную версию JDK..."
            #sudo apt install -y openjdk-$JAVA_VERSION-jdk
            
            # Обновляем переменные
            #export JAVA_HOME=/usr/lib/jvm/java-$JAVA_VERSION-openjdk-amd64
            #export PATH=$JAVA_HOME/bin:$PATH
        else
            log "Правильная версия JDK уже установлена"
        fi
    fi
    
    # Убеждаемся, что JAVA_HOME установлен
    if [ -z "$JAVA_HOME" ]; then
        log "Установлена переменная JAVA_HOME: $JAVA_HOME"
    fi
}

# инициализирую (в принципе необязательно, но для красоты пусть будет)
init() {
    log "Инициализация сборки Spring Boot проекта: Veholder"
    log "Директория проекта: $PROJECT_DIR"
    
    # Проверяем существование директории проекта
    if [ ! -d "$PROJECT_DIR" ]; then
        log "Директория проекта не существует: $PROJECT_DIR"
        exit 1
    fi
    
    # Переходим в директорию проекта
    cd "$PROJECT_DIR" || {
        log "Не удалось перейти в директорию проекта"
        exit 1
    }
    
    log "Текущая директория: $(pwd)"
    
	# Проверяем джаву
	setup_java
	
    # проверяем Maven
    if command -v mvn &> /dev/null; then
        MAVEN_CMD="mvn"
        log "Используем системный Maven"
    else
        log "Maven не найден! Установите maven"
        exit 1
    fi
}

# Функция сборки проекта
build() {
    log "Запускаем сборку проекта..."
    
    # Выполняем сборку
    $MAVEN_CMD clean package -DskipTests
    
    # Проверяем результат сборки
    if [ $? -eq 0 ]; then
        log "Сборка завершена успешно!"
        
        # Показываем информацию о собранном JAR
        JAR_FILE=$(ls target/*.jar 2>/dev/null | head -n 1)
        if [ -f "$JAR_FILE" ]; then
            log "JAR файл: $JAR_FILE"
            log "Размер: $(du -h "$JAR_FILE" | cut -f1)"
        else
            log "JAR файл не найден в target/"
        fi
        return 0
    else
        log "Ошибка при сборке проекта!"
        return 1
    fi
}

# Build Docker image
build_docker_image() {
    log "Building Docker image..."
    
    cd "$PROJECT_DIR" || error_exit "Project directory not found"
    
    # Build Docker image
    docker build -t "$DOCKER_IMAGE_NAME" . || error_exit "Docker build failed"
    docker tag "$DOCKER_IMAGE_NAME" "$DOCKER_IMAGE_NAME:latest"
    
    log "Docker image built successfully: $DOCKER_IMAGE_NAME"
    cd - > /dev/null
}

deploy_to_docker() {
	if [ -f "$LOCAL_JAR_PATH" ]; then
		echo "Copying new JAR to container..."
		docker cp "$LOCAL_JAR_PATH" veholder-app_app_1:"$CONTAINER_JAR_PATH"
		echo "Deployment completed!"
	else
		echo "JAR не найден по пути $LOCAL_JAR_PATH"
		exit 1
	fi
}

# Deploy locally
deploy_local() {
    log "Deploying locally..."
    
    if [ "$USE_DOCKER" = true ]; then
        # Stop existing container
        docker stop "$DOCKER_CONTAINER_NAME" 2>/dev/null || true
        docker rm "$DOCKER_CONTAINER_NAME" 2>/dev/null || true
        
        # Run new container
        docker run -d \
            --name "$DOCKER_CONTAINER_NAME" \
            -p "$LOCAL_PORT:8080" \
            "$DOCKER_IMAGE_NAME:latest" \
            || error_exit "Failed to start Docker container"
            
        log "Application deployed locally via Docker on port $LOCAL_PORT"
    else
        # Deploy directly with Java
        cd "$PROJECT_DIR" || error_exit "Project directory not found"
        
        # Stop existing process
        pkill -f "java -jar.*$JAR_FILE" 2>/dev/null || true
        
        # Start new process
        nohup java -jar "$JAR_FILE" --server.port="$LOCAL_PORT" > app.log 2>&1 &
        
        log "Application deployed locally on port $LOCAL_PORT"
        cd - > /dev/null
    fi
}

# Функция деплоя (заглушка)
deploy() {
    log "Деплой..."
  	deploy_to_docker
  	log "Deployment completed successfully!"
	
    return 0
}

# Главная функция
main() {
    log "=== НАЧАЛО РАБОТЫ СКРИПТА ==="
    
    # Инициализация
    init
    
    # Шаг 1: Сборка
    log "--- ШАГ 1: СБОРКА ---"
    if build; then
        log "Сборка выполнена успешно"
    else
        log "Сборка завершена с ошибкой"
        exit 1
    fi
    
    # Шаг 2: Деплой (заглушка)
    log "--- ШАГ 2: ДЕПЛОЙ ---"
    if deploy; then
        log "Деплой выполнен успешно"
    else
        log "Деплой завершился с ошибкой"
        exit 1
    fi
    
    log "=== РАБОТА СКРИПТА ЗАВЕРШЕНА УСПЕШНО ==="
}

# Обработка сигналов для graceful shutdown
trap 'log "Скрипт прерван пользователем"; exit 1' INT TERM

# Запуск главной функции
main "$@"