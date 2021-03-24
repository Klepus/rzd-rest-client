package com.github.klepus.repository;

import com.github.klepus.model.UserTrainSubscription;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository {
    //TODO: JPA DATA REPO

    List<UserTrainSubscription> findByChatId(long chatId);

    List<UserTrainSubscription> findByChatIdAndTrainNumberAndDateDepart(long chatId, String trainNumber, String dateDepart);

    List<UserTrainSubscription> findAll();

    void save(UserTrainSubscription usersSubscription);

    void deleteById(String subscriptionID);

    String getTrainNumber();

    Optional<UserTrainSubscription> findById(String subscriptionID);
}
