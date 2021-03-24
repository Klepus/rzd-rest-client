package com.github.klepus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Station {

    @JsonProperty(value = "n")
    private String name;
    @JsonProperty(value = "c")
    private Integer code;

    public Station() {
    }

    public Station(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
