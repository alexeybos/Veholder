package org.skillsmart.veholder;

import org.springframework.boot.SpringApplication;
//import org.testcontainers.utility.TestcontainersConfiguration;

public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.from(VeholderApplication::main) // Application - ваш основной класс
                .with(TestContainersConfig.class)
                .run(args);
    }
}
