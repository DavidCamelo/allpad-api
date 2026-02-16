package io.allpad.pad.api;

import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.service.HistoryService;
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

@Tag(name = "History API")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "authorization")
public class HistoryController {
    private final HistoryService historyService;

    @Operation(summary = "Create", description = "Create a new history")
    @PostMapping(value = "/api/{version}/histories", version = "v1")
    public ResponseEntity<HistoryDTO> create(@RequestBody HistoryDTO historyDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.create(historyDTO));
    }

    @Operation(summary = "Get by id", description = "Get history by id")
    @GetMapping(value = "/api/{version}/histories/{id}", version = "v1")
    public ResponseEntity<HistoryDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(historyService.getById(id));
    }

    @Operation(summary = "Get all", description = "Get all histories")
    @GetMapping(value = "/api/{version}/histories", version = "v1")
    public ResponseEntity<List<HistoryDTO>> getAll() {
        return ResponseEntity.ok(historyService.getAll());
    }

    @Operation(summary = "Get histories", description = "Get histories by pad id and file id")
    @GetMapping(value = "/api/{version}/pads/{padId}/files/{fileId}/histories", version = "v1")
    public ResponseEntity<List<HistoryDTO>> getHistoriesByPadIdAndFileId(@PathVariable UUID padId, @PathVariable UUID fileId) {
        return ResponseEntity.ok(historyService.getHistoriesByPadIdAndFileId(padId, fileId));
    }

    @Operation(summary = "Update", description = "Update history")
    @PutMapping(value = "/api/{version}/histories/{id}", version = "v1")
    public ResponseEntity<HistoryDTO> update(@PathVariable UUID id, @RequestBody HistoryDTO historyDTO) {
        return ResponseEntity.ok(historyService.update(id, historyDTO));
    }

    @Operation(summary = "Delete", description = "Delete a history by id")
    @DeleteMapping(value = "/api/{version}/histories/{id}", version = "v1")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        historyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
