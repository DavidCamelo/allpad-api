package io.allpad.pad.api;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.service.FileService;
import io.allpad.pad.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "File API")
@RestController
@RequestMapping("/api/{version}/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "authorization")
public class FileController {
    private final FileService fileService;
    private final HistoryService historyService;

    @Operation(summary = "Create", description = "Create a new file")
    @PostMapping
    public ResponseEntity<FileDTO> create(@RequestBody FileDTO fileDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.create(fileDTO));
    }

    @Operation(summary = "Get by id", description = "Get file by id")
    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all", description = "Get all files")
    @GetMapping
    public ResponseEntity<List<FileDTO>> getAll() {
        return ResponseEntity.ok(fileService.getAll());
    }

    @Operation(summary = "Get files", description = "Get files by pad id")
    @GetMapping("/{id}/histories")
    public ResponseEntity<List<HistoryDTO>> getHistoriesByFileId(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getHistoriesByFileId(id));
    }

    @Operation(summary = "Update", description = "Update file")
    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> update(@PathVariable Long id, @RequestBody FileDTO fileDTO) {
        return ResponseEntity.ok(fileService.update(id, fileDTO));
    }

    @Operation(summary = "Delete", description = "Delete a file by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
