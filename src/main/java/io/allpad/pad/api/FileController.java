package io.allpad.pad.api;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.service.FileService;
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

@Tag(name = "File API")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "authorization")
public class FileController {
    private final FileService fileService;

    @Operation(summary = "Create", description = "Create a new file")
    @PostMapping(value = "/api/{version}/files", version = "v1")
    public ResponseEntity<FileDTO> create(@RequestBody FileDTO fileDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.create(fileDTO));
    }

    @Operation(summary = "Get by id", description = "Get file by id")
    @GetMapping(value = "/api/{version}/files/{id}", version = "v1")
    public ResponseEntity<FileDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.getById(id));
    }

    @Operation(summary = "Get files by pad", description = "Get files by pad id")
    @GetMapping(value = "/api/{version}/pads/{padId}/files", version = "v1")
    public ResponseEntity<List<FileDTO>> getFilesByPadId(@PathVariable UUID padId) {
        return ResponseEntity.ok(fileService.getFilesByPadId(padId));
    }

    @Operation(summary = "Update", description = "Update file")
    @PutMapping(value = "/api/{version}/files/{id}", version = "v1")
    public ResponseEntity<FileDTO> update(@PathVariable UUID id, @RequestBody FileDTO fileDTO) {
        return ResponseEntity.ok(fileService.update(id, fileDTO));
    }

    @Operation(summary = "Delete", description = "Delete a file by id")
    @DeleteMapping(value = "/api/{version}/files/{id}", version = "v1")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
