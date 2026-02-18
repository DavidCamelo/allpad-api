package io.allpad.stripe.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.ProductListParams;
import io.allpad.stripe.config.StripeProperties;
import io.allpad.stripe.dto.PlanDTO;
import io.allpad.stripe.dto.PlanLimitsDTO;
import io.allpad.stripe.error.PlanNotFoundException;
import io.allpad.stripe.service.PlanService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final StripeProperties stripeProperties;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }

    @Override
    public PlanDTO getCurrentPlan() {
        return getFreePlan();
    }

    @Override
    public List<PlanDTO> getAllPlans() {
        try {
            var productParams = ProductListParams.builder().build();
            var products = Product.list(productParams);
            List<PlanDTO> planList = new ArrayList<>();
            planList.add(getFreePlan());
            planList.addAll(products.getData().stream()
                    .filter(Product::getActive)
                    .map(product -> {
                        try {
                            var price = Price.retrieve(product.getDefaultPrice());
                            return PlanDTO.builder()
                                    .id(product.getId())
                                    .name(product.getName())
                                    .description(product.getDescription())
                                    .priceId(price.getId())
                                    .amount(price.getUnitAmount() / 100)
                                    .unitAmount(price.getUnitAmount())
                                    .currency(price.getCurrency())
                                    .interval(price.getRecurring().getInterval())
                                    .planLimits(getPlanLimits(product.getName()))
                                    .build();
                        } catch (StripeException e) {
                            throw new PlanNotFoundException(String.format("Failed to list plans: %s", e.getMessage()));
                        }
                    })
                    .sorted(Comparator.comparing(PlanDTO::amount))
                    .toList());
            return planList;
        } catch (StripeException e) {
            throw new PlanNotFoundException(String.format("Failed to list plans: %s", e.getMessage()));
        }
    }

    private PlanDTO getFreePlan() {
        return PlanDTO.builder()
                .id("free")
                .name("Free")
                .description("Free plan includes: 1 pad, 5 files by pad, 5 history by file")
                .priceId("free")
                .amount(0L)
                .currency("")
                .interval("")
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
