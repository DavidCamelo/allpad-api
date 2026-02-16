package io.allpad.pad.api;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.service.PadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pad API")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "authorization")
public class PadController {
    private final PadService padService;

    @Operation(summary = "Create", description = "Create a new pad")
    @PostMapping(value = "/api/{version}/pads", version = "v1")
    public ResponseEntity<PadDTO> create(@RequestBody PadDTO padDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(padService.create(padDTO));
    }

    @Operation(summary = "Get by id", description = "Get pad by id")
    @GetMapping(value = "/api/{version}/pads/{id}", version = "v1")
    public ResponseEntity<PadDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(padService.getById(id));
    }

    @Operation(summary = "Get all", description = "Get all pads")
    @GetMapping(value = "/api/{version}/pads", version = "v1")
    public ResponseEntity<List<PadDTO>> getAll() {
        return ResponseEntity.ok(padService.getAll());
    }

    @Operation(summary = "Update", description = "Update pad")
    @PutMapping(value = "/api/{version}/pads/{id}", version = "v1")
    public ResponseEntity<PadDTO> update(@PathVariable UUID id, @RequestBody PadDTO padDTO) {
        return ResponseEntity.ok(padService.update(id, padDTO));
    }

    @Operation(summary = "Delete", description = "Delete a pad by id")
    @DeleteMapping(value = "/api/{version}/pads/{id}", version = "v1")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        padService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
