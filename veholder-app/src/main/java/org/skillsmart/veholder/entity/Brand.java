package org.skillsmart.veholder.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.reactivestreams.Publisher;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Schema(description = "Сущность пользователя")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор бренда", example = "124523", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Имя бренда", example = "Москвич")
    private String name;

    @Schema(description = "Тип т/с", example = "легковая")
    private String type;

    @Schema(description = "Грузоподъемность", example = "1500")
    private int loadCapacity;

    @Schema(description = "объем бака", example = "50")
    private int tank;

    @Schema(description = "Количество мест в салоне", example = "5")
    private int numberOfSeats;

    public Brand() {
    }

    public Brand(Long id) {
        this.id = id;
    }

    public Brand(Long id, Brand brand) {
        this.id = id;
        this.name = brand.getName();
        this.type = brand.getType();
        this.loadCapacity = brand.getLoadCapacity();
        this.tank = brand.getTank();
        this.numberOfSeats = brand.getNumberOfSeats();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(int loadCapacity) {
        this.loadCapacity = loadCapacity;
    }

    public int getTank() {
        return tank;
    }

    public void setTank(int tank) {
        this.tank = tank;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", loadCapacity=" + loadCapacity +
                ", tank=" + tank +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }
}
