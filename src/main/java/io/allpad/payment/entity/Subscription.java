package io.allpad.payment.entity;

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
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String customerId; // Customer ID in Stripe (cus_...)
    private String subscriptionId; // Subscription ID in Stripe (sub_...)
    private String planId; // The product ID
    private String priceId; //price ID
    private String status; // active, incomplete, canceled, etc.
    private String invoiceStatus; // paid, unpaid, etc.
    private Long currentPeriodEnd; // Timestamp for when the current period ends
}
