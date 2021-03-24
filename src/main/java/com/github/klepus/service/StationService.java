package com.github.klepus.service;

import com.github.klepus.cache.StationCache;
import com.github.klepus.model.Station;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final RestTemplate restTemplate;
    private final StationCache stationCache;
    private final String requestURL = "https://pass.rzd.ru/suggester?lang=ru&stationNamePart=%s";

    public StationService(RestTemplate restTemplate, StationCache stationCach) {
        this.restTemplate = restTemplate;
        this.stationCache = stationCach;
    }

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCode = stationCache.getStationCode(stationNameParam);
        if (stationCode.isPresent()) {
            return stationCode.get();
        }

        if (fetchStationsByPartOfName(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationCache.getStationCode(stationNameParam).orElse(-1);
    }


    public Optional<List<Station>> fetchStationsByPartOfName(String partOfName) {
        ResponseEntity<List<Station>> responseEntity =
                restTemplate.exchange(String.format(requestURL, partOfName.toUpperCase()), HttpMethod.GET, null, new ParameterizedTypeReference<List<Station>>() {
                });
        List<Station> stations = responseEntity.getBody();

        if (stations != null) {
            stations.forEach(station -> stationCache.add(station.getName(), station.getCode()));
        }

        return Optional.ofNullable(stations);
    }
}
