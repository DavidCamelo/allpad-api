package io.allpad.stripe.service;

import java.util.List;

import io.allpad.stripe.dto.PlanDTO;

public interface PlanService {
    PlanDTO getCurrentPlan();

    List<PlanDTO> getAllPlans();
}
