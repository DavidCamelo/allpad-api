package io.allpad.stripe.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.ProductListParams;
import io.allpad.auth.utils.ContextUtils;
import io.allpad.stripe.config.StripeProperties;
import io.allpad.stripe.dto.PlanDTO;
import io.allpad.stripe.dto.PlanLimitsDTO;
import io.allpad.stripe.repository.StripeSubscriptionRepository;
import io.allpad.stripe.service.PlanService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final StripeProperties stripeProperties;
    private final StripeSubscriptionRepository stripeSubscriptionRepository;
    private final ContextUtils contextUtils;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }

    @Override
    public PlanDTO getCurrentPlan() {
        try {
            var existingSubscription = stripeSubscriptionRepository.findByUser(contextUtils.getUser());
            if (existingSubscription.isPresent() && "active".equals(existingSubscription.get().getStatus())) {
                var stripeSubscription = existingSubscription.get();
                var product = Product.retrieve(stripeSubscription.getPlanId());
                return getPlan(product, stripeSubscription.getStripeSubscriptionId());
            }
        } catch (StripeException e) {
            log.error("Failed to get plan: {}", e.getMessage());
        }
        return getFreePlan();
    }

    @Override
    public List<PlanDTO> getAllPlans() {
        List<PlanDTO> planList = new ArrayList<>();
        try {
            var productParams = ProductListParams.builder().build();
            var products = Product.list(productParams);
            planList.add(getFreePlan());
            planList.addAll(products.getData().stream()
                    .filter(Product::getActive)
                    .map(this::getPlan)
                    .sorted(Comparator.comparing(PlanDTO::amount))
                    .toList());
        } catch (StripeException e) {
            log.error("Failed to list plans: {}", e.getMessage());
        }
        return planList;
    }

    private PlanDTO getPlan(Product product) {
        return getPlan(product, null);
    }

    private PlanDTO getPlan(Product product, String subscriptionId) {
        try {
            var price = Price.retrieve(product.getDefaultPrice());
            return PlanDTO.builder()
                    .id(product.getId())
                    .priceId(price.getId())
                    .subscriptionId(subscriptionId)
                    .name(product.getName())
                    .description(product.getDescription())
                    .amount(price.getUnitAmount())
                    .unitAmount(price.getUnitAmount())
                    .currency(price.getCurrency())
                    .interval(price.getRecurring().getInterval())
                    .planLimits(getPlanLimits(product.getName()))
                    .build();
        } catch (StripeException e) {
            log.error("Failed to get plan: {}", e.getMessage());
        }
        return getFreePlan();
    }

    private PlanDTO getFreePlan() {
        return PlanDTO.builder()
                .id("free")
                .name("Free")
                .description("Free plan includes: 1 pad, 5 files by pad, 5 history by file")
                .priceId("free")
                .amount(0L)
                .unitAmount(0L)
                .currency("usd")
                .interval("month")
                .planLimits(getPlanLimits("Free"))
                .build();
    }

    private PlanLimitsDTO getPlanLimits(String name) {
        return switch (name) {
            case "Free" -> PlanLimitsDTO.builder().pads(1).filesPerPad(5).historiesPerFile(5).build();
            case "Basic" -> PlanLimitsDTO.builder().pads(10).filesPerPad(5).historiesPerFile(5).build();
            case "Standard" -> PlanLimitsDTO.builder().pads(20).filesPerPad(10).historiesPerFile(10).build();
            case "Premium" -> PlanLimitsDTO.builder().pads(100).filesPerPad(100).historiesPerFile(100).build();
            default -> null;
        };
    }
}
