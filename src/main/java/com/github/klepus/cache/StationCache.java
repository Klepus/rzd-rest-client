package com.github.klepus.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StationCache {

    private Map<String, Integer> cache = new HashMap<>();


    public Optional<Integer> getStationCode(String stationNameParam) {
        return Optional.ofNullable(cache.get(stationNameParam));
    }

    public Optional<String> getStationName(String stationNameParam) {
        return cache.keySet().stream().filter(name -> name.equals(stationNameParam)).findFirst();
    }

    public void add(String name, Integer code) {
        cache.put(name, code);
    }
}
