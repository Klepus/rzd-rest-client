package com.github.klepus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {

    @JsonProperty(value = "number")
    private String number;
    @JsonProperty(value = "brand")
    private String brand;
    @JsonProperty(value = "station0")
    private String stationDepart;
    @JsonProperty(value = "station1")
    private String stationArrival;
    @JsonProperty(value = "date0")
    private String dateDepart;
    @JsonProperty(value = "date1")
    private String dateArrival;
    @JsonProperty(value = "time0")
    private String timeDepart;
    @JsonProperty(value = "time1")
    private String timeArrival;
    @JsonProperty(value = "cars")
    private List<Car> availableCars;
    @JsonProperty(value = "timeInWay")
    private String timeInWay;

    public Train() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStationDepart() {
        return stationDepart;
    }

    public void setStationDepart(String stationDepart) {
        this.stationDepart = stationDepart;
    }

    public String getStationArrival() {
        return stationArrival;
    }

    public void setStationArrival(String stationArrival) {
        this.stationArrival = stationArrival;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getDateArrival() {
        return dateArrival;
    }

    public void setDateArrival(String dateArrival) {
        this.dateArrival = dateArrival;
    }

    public String getTimeDepart() {
        return timeDepart;
    }

    public void setTimeDepart(String timeDepart) {
        this.timeDepart = timeDepart;
    }

    public String getTimeArrival() {
        return timeArrival;
    }

    public void setTimeArrival(String timeArrival) {
        this.timeArrival = timeArrival;
    }

    public List<Car> getAvailableCars() {
        return availableCars;
    }

    public void setAvailableCars(List<Car> availableCars) {
        this.availableCars = availableCars;
    }

    public String getTimeInWay() {
        return timeInWay;
    }

    public void setTimeInWay(String timeInWay) {
        this.timeInWay = timeInWay;
    }

    @Override
    public String toString() {
        return "Train{" +
                "number='" + number + '\'' +
                ", brand='" + brand + '\'' +
                ", stationDepart='" + stationDepart + '\'' +
                ", stationArrival='" + stationArrival + '\'' +
                ", dateDepart='" + dateDepart + '\'' +
                ", dateArrival='" + dateArrival + '\'' +
                ", timeDepart='" + timeDepart + '\'' +
                ", timeArrival='" + timeArrival + '\'' +
                ", availableCars=" + availableCars +
                ", timeInWay='" + timeInWay + '\'' +
                '}';
    }
}
