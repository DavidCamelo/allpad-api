package io.allpad.payment.repository;

import io.allpad.auth.entity.User;
import io.allpad.payment.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUser(User user);

    Optional<Subscription> findByCustomerId(String customerId);

    Optional<Subscription> findBySubscriptionId(String subscriptionId);
}
