package org.skillsmart.veholder;

import io.gatling.javaapi.core.Simulation;

public class EnterpriseVehiclesPerformanceTest extends Simulation {

    // Конфигурация теста
    private static final int MAX_RPS = 1000; // Максимальная целевая нагрузка (1000 RPS = 1 krps)
    private static final int RAMP_DURATION = 300; // Время наращивания нагрузки в секундах
    private static final int PEAK_DURATION = 600; // Время удержания пиковой нагрузки
    private static final int TOTAL_TEST_DURATION = 1200; // Общая длительность теста

    public EnterpriseVehiclesPerformanceTest() {

    }
}
