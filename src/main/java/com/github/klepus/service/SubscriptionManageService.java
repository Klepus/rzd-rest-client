package com.github.klepus.service;

import com.github.klepus.model.UserTrainSubscription;
import com.github.klepus.repository.UserSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@Service
public class SubscriptionManageService {

    private final UserSubscriptionRepository subscriptionRepository;

    public SubscriptionManageService(UserSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<UserTrainSubscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public void saveSubscription(UserTrainSubscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void deleteSubscription(String subscriptionID) {
        subscriptionRepository.deleteById(subscriptionID);
    }


    public boolean hasTrainsSubscription(UserTrainSubscription subscription) {
        return subscriptionRepository.findByChatIdAndTrainNumberAndDateDepart(subscription.getChatId(),
                subscription.getTrainNumber(), subscription.getDateDepart()).size() > 0;
    }

    public Optional<UserTrainSubscription> getSubscriptionById(String subscriptionId) {
        return subscriptionRepository.findById(subscriptionId);
    }

    public List<UserTrainSubscription> getSubscriptionsByChatId(long chatId) {
        return subscriptionRepository.findByChatId(chatId);
    }
}
