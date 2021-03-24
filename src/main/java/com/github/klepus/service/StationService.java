package com.github.klepus.service;

import com.github.klepus.cache.StationCache;
import com.github.klepus.model.Station;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final RestTemplate restTemplate;
    private final StationCache stationCache;
    private final MessageService localeMessageService;
    private final String requestURL = "https://pass.rzd.ru/suggester?lang=ru&stationNamePart=%s";

    public StationService(RestTemplate restTemplate, StationCache stationCach, MessageService localeMessageService) {
        this.restTemplate = restTemplate;
        this.stationCache = stationCach;
        this.localeMessageService = localeMessageService;
    }

    public int getCodeByFullName(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCode = stationCache.getStationCode(stationNameParam);
        if (stationCode.isPresent()) {
            return stationCode.get();
        }

        if (fetchByPartOfName(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationCache.getStationCode(stationNameParam).orElse(-1);
    }

    public void searchByPartOfName(String stationName) {
        String upperCaseStationName = stationName.toUpperCase();
        Optional<String> optionalStationName = stationCache.getStationName(upperCaseStationName);

        if (optionalStationName.isPresent()) {
            //TODO: Send to telegram "Станция найдена"
            String message = localeMessageService.getMessage("reply.stationBook.stationFound","Emoji ;) ", optionalStationName.get());
            System.out.println(message);
            return;
        }
        System.out.println("Нет в кэше, делаю запрос:");

        List<Station> stations = fetchByPartOfName(stationName).orElse(Collections.emptyList());
        List<String> foundedNames = stations.stream()
                .map(Station::getName)
                .filter(name -> name.contains(upperCaseStationName))
                .collect(Collectors.toList());
        if (foundedNames.isEmpty()) {
            //TODO: Send to telegram "Станция не найдена"
            String message = localeMessageService.getMessage("reply.trainSearch.stationNotFound","Emoji :( ");
            System.out.println(message);
            return;
        }
        //TODO: Send to telegram "Станции найдены:" и вывести список станций
        String message = localeMessageService.getMessage("reply.stationBook.stationsFound","Emoji o_O ", foundedNames);
        System.out.println(message);
    }

    private Optional<List<Station>> fetchByPartOfName(String partOfName) {
        System.out.println("fetching...");
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
