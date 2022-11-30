package ru.practicum.ewm.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.users.model.Subscription;
import javax.transaction.Transactional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findByUserIdAndSubscriberId(Long userId, Long subscriberId);

    @Modifying
    @Transactional
    @Query("delete from Subscription s where s.subscriberId = ?1")
    void deleteAllByUserId(Long userId);
}
