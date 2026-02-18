package io.allpad.stripe.entity;

import io.allpad.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class StripeSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String stripeCustomerId; // Customer ID in Stripe (cus_...)
    private String stripeSubscriptionId; // Subscription ID in Stripe (sub_...)
    private String planId; // The plan/price ID (price_...)
    private String status; // active, incomplete, canceled, etc.
    private Long currentPeriodEnd; // Timestamp for when the current period ends
}
