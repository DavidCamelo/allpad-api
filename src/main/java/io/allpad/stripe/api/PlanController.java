package io.allpad.stripe.api;

import io.allpad.stripe.dto.PlanDTO;
import io.allpad.stripe.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Plans API")
@RestController
@RequestMapping(value = "api/{version}/plans", version = "v1")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @Operation(summary = "Get current plan", description = "Get current plan of the authenticated user")
    @GetMapping("current")
    public ResponseEntity<PlanDTO> getCurrentPlan() {
        return ResponseEntity.ok(planService.getCurrentPlan());
    }

    @Operation(summary = "Get all plans", description = "Get all plans from stripe")
    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
}
