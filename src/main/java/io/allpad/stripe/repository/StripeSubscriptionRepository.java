package io.allpad.stripe.repository;

import io.allpad.auth.entity.User;
import io.allpad.stripe.entity.StripeSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StripeSubscriptionRepository extends JpaRepository<StripeSubscription, UUID> {
    Optional<StripeSubscription> findByUser(User user);

    Optional<StripeSubscription> findByStripeCustomerId(String stripeCustomerId);

    Optional<StripeSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
