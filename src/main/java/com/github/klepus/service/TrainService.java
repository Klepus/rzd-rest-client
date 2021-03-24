package com.github.klepus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klepus.model.Train;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrainService {

    @Autowired
    private  RestTemplate restTemplate;
    private final String requestRidURL = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&dir=0&tfl=3&checkSeats=0&code0={STATION_DEPART_CODE}&dt0={DATE_DEPART}&code1={STATION_ARRIVAL_CODE}";
    private final String requestTrainsWithRidURL = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&rid={RID_VALUE}";

    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";
    private static final String TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE = "находится за пределами периода";

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Train> getTrainsByParams(int departureStationCode, int arrivalStationCode, String departDate) {
        List<Train> trains;
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(departureStationCode));
        urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(arrivalStationCode));
        urlParams.put(URI_PARAM_DATE_DEPART, departDate);

        Map<String, HttpHeaders> ridAndHttpHeaders = fetchRidAndHeaders(urlParams);
        if (ridAndHttpHeaders.isEmpty()) {
            return Collections.emptyList();
        }

        String rid = ridAndHttpHeaders.keySet().iterator().next();
        HttpHeaders httpHeaders = ridAndHttpHeaders.get(rid);
        List<String> cookies =httpHeaders.get("Set-Cookie");

        if (cookies == null) {
            //TODO: Send to telegram error "Не могу обработать ваш запрос."
            System.out.println("Не могу обработать запрос.");
            return Collections.emptyList();
        }

        HttpHeaders trainHeaders = fetchDataHeaders(cookies);
        String trainsResponseBody = fetchTrainsInfo(rid, trainHeaders);

        trains = parseResponseBody(trainsResponseBody);

        return trains;
    }

    private List<Train> parseResponseBody(String trainsResponseBody) {
        List<Train> trainList = null;
        try {
            JsonNode trainsNode = objectMapper.readTree(trainsResponseBody).path("tp").findPath("list");
            trainList = Arrays.asList(objectMapper.readValue(trainsNode.toString(), Train[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Objects.isNull(trainList) ? Collections.emptyList() : trainList;
    }

    private String fetchTrainsInfo(String rid, HttpHeaders trainHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>(trainHeaders);
        ResponseEntity<String> resultResponse = restTemplate.exchange(requestTrainsWithRidURL,
                HttpMethod.GET,
                httpEntity,
                String.class, rid);

        while (isResponseResultRidDuplicate(resultResponse)) {
            resultResponse = restTemplate.exchange(requestTrainsWithRidURL,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, rid);
        }

        return resultResponse.getBody();
    }


    //Срабатывает если RZD ответил снова RID, а не данными по поезду
    private boolean isResponseResultRidDuplicate(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody() == null) {
            return true;
        }
        return resultResponse.getBody().contains("\"result\":\"RID");
    }

    private HttpHeaders fetchDataHeaders(List<String> cookies) {
        String jSessionId = cookies.get(cookies.size() - 1);
        jSessionId = jSessionId.substring(jSessionId.indexOf("=") + 1, jSessionId.indexOf(";"));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "lang=ru");
        requestHeaders.add("Cookie", "JSESSIONID=" + jSessionId);
        requestHeaders.add("Cookie", "AuthFlag=false");

        return requestHeaders;
    }

    private Map<String, HttpHeaders> fetchRidAndHeaders(Map<String, String> urlParams) {
        ResponseEntity<String> responseEntity =
            restTemplate.getForEntity(requestRidURL, String.class, urlParams);

        String responseBody = responseEntity.getBody();
        if (responseBodyHasNoTrains(responseBody)) {
            //TODO: Send to telegram "Дата отправления находится за пределами периода предварительной продажи."
            System.out.println("Дата отправления находится за пределами периода предварительной продажи.");
            return Collections.emptyMap();
        }

        Optional<String> parseRid = parseRid(responseBody);
        if (parseRid.isEmpty()) {
            return Collections.emptyMap();
        }

        return Collections.singletonMap(parseRid.get(), responseEntity.getHeaders());
    }

    private Optional<String> parseRid(String responseBody) {
        String rid = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody.trim());
            JsonNode ridNode = jsonNode.get("RID");
            if (ridNode != null) {
                rid = ridNode.asText();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(rid);
    }

    private boolean responseBodyHasNoTrains(String responseBody) {
        return responseBody == null || responseBody.contains(TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE);
    }
}
