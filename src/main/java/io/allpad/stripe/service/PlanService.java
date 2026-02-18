package io.allpad.stripe.service;

import java.util.List;

import io.allpad.stripe.dto.PlanDTO;

public interface PlanService {
    List<PlanDTO> getAllPlans();

    PlanDTO getCurrentPlan();
}
