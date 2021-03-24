package com.github.klepus.service;

import com.github.klepus.model.Car;
import com.github.klepus.model.Train;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TrainService trainService;
    private final MessageService messageService;

    public TicketService(TrainService trainService, MessageService messageService) {
        this.trainService = trainService;
        this.messageService = messageService;
    }

    public void sendTicketInfo(List<Train> trains) {
        for (Train train : trains) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = trainService.filterCarsWithMinimumPrice(train.getAvailableCars());
            train.setAvailableCars(carsWithMinimalPrice);

            for (Car car : carsWithMinimalPrice) {
                carsInfo.append(messageService.getMessage("subscription.carsTicketsInfo",
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String ticketInfoMessage = messageService.getMessage("reply.trainSearch.trainInfo",
                    "Emojis TRAIN! ", train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    "Emojis CLOCKS... ", train.getTimeInWay(), carsInfo);

            //TODO: Send to telegram with Subscribe button
            System.out.println(ticketInfoMessage);
        }
        //TODO: Add founded trains to cache by user
    }
}
