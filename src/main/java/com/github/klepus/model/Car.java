package com.github.klepus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    @JsonProperty(value = "type")
    private String carType;
    @JsonProperty(value = "freeSeats")
    private int freeSeats;
    @JsonProperty(value = "tariff")
    private int minimalPrice;

    public Car() {
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public int getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(int minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carType='" + carType + '\'' +
                ", freeSeats=" + freeSeats +
                ", minimalPrice=" + minimalPrice +
                '}';
    }
}
