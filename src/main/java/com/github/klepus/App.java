package com.github.klepus;

import com.github.klepus.model.Train;
import com.github.klepus.service.StationService;
import com.github.klepus.config.AppConfig;
import com.github.klepus.service.TrainService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        StationService stationService = applicationContext.getBean("stationService", StationService.class);
        TrainService trainService = applicationContext.getBean("trainService", TrainService.class);

        System.out.println(stationService.getCodeByFullName("мосва"));
        System.out.println(stationService.getCodeByFullName("москва"));
        System.out.println(stationService.getCodeByFullName("москва"));
        System.out.println(stationService.getCodeByFullName("москоу"));
        System.out.println(stationService.getCodeByFullName("Иваново"));
        System.out.println(stationService.getCodeByFullName("Ивановофыв"));

        int departure = stationService.getCodeByFullName("москва");
        int arrival = stationService.getCodeByFullName("Иваново");
        String date = "24.03.2021";

        List<Train> trainList = trainService.getTrainsByParams(departure, arrival, date);
        trainList.forEach(System.out::println);
        date = "24.03.2022";
        trainList = trainService.getTrainsByParams(departure, arrival, date);
        trainList.forEach(System.out::println);

        stationService.searchByPartOfName("москва");
        stationService.searchByPartOfName("иваново");
        stationService.searchByPartOfName("САНКТ-ПЕТЕРБУРГ");
        stationService.searchByPartOfName("иваново");
        stationService.searchByPartOfName("САНКТ-ПЕТЕРБУРГ");
        stationService.searchByPartOfName("САНasd");
    }
}
