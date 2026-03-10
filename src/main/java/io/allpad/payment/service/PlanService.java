package io.allpad.payment.service;

import java.util.List;

import io.allpad.payment.dto.PlanDTO;

public interface PlanService {
    PlanDTO getCurrentPlan();

    List<PlanDTO> getAllPlans();
}
