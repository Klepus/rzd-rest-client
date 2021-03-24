package com.github.klepus.service;

import com.github.klepus.model.Car;
import com.github.klepus.model.Train;
import com.github.klepus.model.UserTrainSubscription;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

//@Service
public class SubscriptionNotificationService {

    private final StationService stationService;
    private final TrainService trainService;
    private final TicketService ticketService;
    private final SubscriptionManageService subscriptionManageService;
    private final PrepareMessageService prepareMessageService;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


    public SubscriptionNotificationService(StationService stationService, TrainService trainService, TicketService ticketService,
                                           SubscriptionManageService subscriptionManageService, PrepareMessageService prepareMessageService) {
        this.stationService = stationService;
        this.trainService = trainService;
        this.ticketService = ticketService;
        this.subscriptionManageService = subscriptionManageService;
        this.prepareMessageService = prepareMessageService;
    }

    public void runAllSubscriptions() {
        while (true) {
            subscriptionManageService.getAllSubscriptions().forEach(this::processSubscription);
            //TODO: Scheduling
            try {
                Thread.sleep(60_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processSubscription(UserTrainSubscription subscription) {
        List<Train> currentTrains = getCurrentTrains(subscription);

        if (isDeparted(currentTrains, subscription)) {
            subscriptionManageService.deleteSubscription(subscription.getId());
            //TODO: Send to telegram
            String message = prepareMessageService.getMessage("subscription.trainHasDeparted",
                    "Emojis :( ", subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getDateDepart(), subscription.getTimeDepart());
            System.out.println(message);
            return;
        }

        currentTrains.forEach(currentTrain -> {
            if (currentTrain.getNumber().equals(subscription.getTrainNumber()) &&
                    currentTrain.getDateDepart().equals(subscription.getDateDepart())) {

                List<Car> actualCarsWithMinimumPrice = trainService.filterCarsWithMinimumPrice(currentTrain.getAvailableCars());

                Map<String, List<Car>> updatedCarsNotification = processCarLists(subscription.getSubscribedCars(),
                        actualCarsWithMinimumPrice);

                if (!updatedCarsNotification.isEmpty()) {
                    String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
                    List<Car> updatedCars = updatedCarsNotification.get(priceChangesMessage);

                    subscription.setSubscribedCars(updatedCars);
                    subscriptionManageService.saveSubscription(subscription);
                    notifyUser(subscription, priceChangesMessage, updatedCars);
                }
            }
        });
    }

    private void notifyUser(UserTrainSubscription subscription, String priceChangesMessage, List<Car> updatedCars) {
        StringBuilder notificationMessage = new StringBuilder(prepareMessageService.getMessage("subscription.trainTicketsPriceChanges",
                "Emojis NOTIFICATION_BELL ", subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival())).append(priceChangesMessage);

        notificationMessage.append(prepareMessageService.getMessage("subscription.lastTicketPrices"));

        for (Car car : updatedCars) {
            notificationMessage.append(prepareMessageService.getMessage("subscription.carsTicketsInfo",
                    car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
        }

        //TODO: Send to telegram
        System.out.println(notificationMessage);
    }

    private Map<String, List<Car>> processCarLists(List<Car> subscribedCars, List<Car> actualCars) {
        StringBuilder notificationMessage = new StringBuilder();

        for (Car subscribedCar : subscribedCars) {

            for (Car actualCar : actualCars) {
                if (actualCar.getCarType().equals(subscribedCar.getCarType())) {

                    if (actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(prepareMessageService.getMessage("subscription.PriceUp", "Emojis PRICE_UP! ",
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(prepareMessageService.getMessage("subscription.PriceDown", "Emojis PRICE_DOWN! ",
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    }
                    subscribedCar.setFreeSeats(actualCar.getFreeSeats());
                }
            }
        }

        return notificationMessage.length() == 0 ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(), subscribedCars);
    }

    private boolean isDeparted(List<Train> currentTrains, UserTrainSubscription subscription) {
        return currentTrains.stream().map(Train::getNumber).noneMatch(Predicate.isEqual(subscription.getTrainNumber()));
    }

    private List<Train> getCurrentTrains(UserTrainSubscription subscription) {
        int stationDepartCode = stationService.getCodeByFullName(subscription.getStationDepart());
        int stationArrivalCode = stationService.getCodeByFullName(subscription.getStationArrival());
        String dateDeparture = subscription.getDateDepart();

        return trainService.getTrainsByParams(stationDepartCode, stationArrivalCode, dateDeparture);
    }
}
