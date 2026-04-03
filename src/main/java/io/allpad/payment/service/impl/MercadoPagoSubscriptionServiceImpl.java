package io.allpad.payment.service.impl;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preapproval.PreapprovalClient;
import com.mercadopago.client.preapproval.PreapprovalCreateRequest;
import com.mercadopago.exceptions.MPApiException;
import io.allpad.auth.entity.User;
import io.allpad.payment.config.MercadoPagoProperties;
import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.repository.SubscriptionRepository;
import io.allpad.payment.service.SubscriptionService;
import io.allpad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import io.allpad.payment.entity.Subscription;
import io.allpad.payment.error.SubscriptionException;

import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoSubscriptionServiceImpl implements SubscriptionService {
    private final MercadoPagoProperties mercadopagoProperties;
    private final SubscriptionRepository subscriptionRepository;
    private final ContextUtils contextUtils;
    private final JsonMapper jsonMapper = new JsonMapper();

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(mercadopagoProperties.accessToken());
    }

    private String getMpPlanId(String planName) {
        if (planName == null)
            return null;
        if (planName.equalsIgnoreCase("Basic"))
            return mercadopagoProperties.preapprovalPlanBasicId();
        if (planName.equalsIgnoreCase("Standard"))
            return mercadopagoProperties.preapprovalPlanStandardId();
        if (planName.equalsIgnoreCase("Premium"))
            return mercadopagoProperties.preapprovalPlanPremiumId();
        return null;
    }

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        var mpPlanId = getMpPlanId(subscriptionDTO.planName());
        if (mpPlanId == null) {
            throw new SubscriptionException(
                    "Invalid plan name or missing preapproval plan ID: " + subscriptionDTO.planName());
        }
        try {
            return callPreapprovalEndpoint(subscriptionDTO, mpPlanId);
        } catch (SubscriptionException e) {
            return callPreapprovalSDK(subscriptionDTO, mpPlanId);
        }
    }

    private SubscriptionDTO callPreapprovalSDK(SubscriptionDTO subscriptionDTO, String mpPlanId) {
        try {
            var user = contextUtils.getUser();
            var client = new PreapprovalClient();
            var request = PreapprovalCreateRequest.builder()
                    // .preapprovalPlanId(mpPlanId)
                    .reason("Subscription to " + subscriptionDTO.planName())
                    .payerEmail(user.getEmail())
                    .backUrl("https://allpad.io")
                    .build();
            var preapproval = client.create(request);
            return saveSubscription(user, subscriptionDTO, preapproval.getId(), preapproval.getInitPoint());
        } catch (Exception e) {
            log.error("Failed to create Mercado Pago subscription", e);
            if (e instanceof MPApiException ex) {
                log.error(jsonMapper.writeValueAsString(ex.getApiResponse()));
            }
            throw new SubscriptionException("Failed to create Mercado Pago subscription: " + e.getMessage());
        }
    }

    private SubscriptionDTO callPreapprovalEndpoint(SubscriptionDTO subscriptionDTO, String mpPlanId) {
        var user = contextUtils.getUser();
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mercadopagoProperties.accessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        var body = new HashMap<>();
        body.put("preapproval_plan_id", mpPlanId);
        body.put("reason", "Subscription to " + subscriptionDTO.planName());
        body.put("payer_email", user.getEmail());
        body.put("back_url", "https://allpad.io");
        var request = new HttpEntity<>(body, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.mercadopago.com/preapproval",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new SubscriptionException("Mercado Pago API error: " + response.getStatusCode());
        }
        return saveSubscription(user, subscriptionDTO, response.getBody().get("id").toString(),
                response.getBody().get("init_point").toString());
    }

    private SubscriptionDTO saveSubscription(User user, SubscriptionDTO subscriptionDTO, String preapprovalId,
            String initPoint) {
        Subscription sub = subscriptionRepository.findByUser(user).orElse(new Subscription());
        sub.setUser(user);
        sub.setSubscriptionId(preapprovalId);
        sub.setPlanId(subscriptionDTO.planId());
        sub.setPriceId(subscriptionDTO.priceId());
        sub.setStatus("pending");
        subscriptionRepository.save(sub);

        return SubscriptionDTO.builder()
                .planId(subscriptionDTO.planId())
                .priceId(subscriptionDTO.priceId())
                .planName(subscriptionDTO.planName())
                .subscriptionId(preapprovalId)
                .clientSecret(initPoint)
                .provider("mercadopago")
                .status("pending")
                .build();
    }

    @Override
    public void cancelSubscription() {
        var user = contextUtils.getUser();
        log.info("Canceling Mercado Pago subscription for user: {}", user.getEmail());
        subscriptionRepository.findByUser(user).ifPresent(subscription -> {
            try {
                PreapprovalClient client = new PreapprovalClient();
                // To cancel a preapproval, send an update with status canceled
                client.update(subscription.getSubscriptionId(),
                        com.mercadopago.client.preapproval.PreapprovalUpdateRequest.builder().status("cancelled")
                                .build());
                subscription.setStatus("cancelled");
                subscriptionRepository.save(subscription);
            } catch (Exception e) {
                log.error("Failed to cancel Mercado Pago subscription", e);
                throw new SubscriptionException("Failed to cancel Mercado Pago subscription: " + e.getMessage());
            }
        });
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        log.info("Handling Mercado Pago webhook request: {}", payload);
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(payload);
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "";

            if ("subscription_preapproval".equals(type) || "preapproval".equals(type)) {
                String id = jsonNode.has("data") ? jsonNode.get("data").get("id").asText() : null;
                if (id != null) {
                    log.info("Received Mercado Pago preapproval webhook for ID {}", id);

                    com.mercadopago.client.preapproval.PreapprovalClient client = new com.mercadopago.client.preapproval.PreapprovalClient();
                    com.mercadopago.resources.preapproval.Preapproval preapproval = client.get(id);

                    subscriptionRepository.findBySubscriptionId(id).ifPresent(sub -> {
                        sub.setStatus(preapproval.getStatus());
                        subscriptionRepository.save(sub);
                        log.info("Updated subscription {} status to {}", id, preapproval.getStatus());
                    });
                }
            }
        } catch (Exception e) {
            log.error("Failed to process Mercado Pago webhook", e);
        }
    }
}
