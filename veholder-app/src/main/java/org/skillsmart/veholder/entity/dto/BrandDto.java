package org.skillsmart.veholder.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сущность пользователя")
public record BrandDto(
        @Schema(description = "Уникальный идентификатор бренда", example = "124523", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,
        @Schema(description = "Имя бренда", example = "Москвич")
        String name,
        @Schema(description = "Тип т/с", example = "легковая")
        String type,
        @Schema(description = "Грузоподъемность", example = "1500")
        int loadCapacity,
        @Schema(description = "объем бака", example = "50")
        int tank,
        @Schema(description = "Количество мест в салоне", example = "5")
        int numberOfSeats
) {}
