package io.allpad.auth.api;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Role API")
@RestController
@RequestMapping(value = "/api/{version}/roles", version = "v1")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Create", description = "Create a new role")
    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(roleDTO));
    }

    @Operation(summary = "Get all", description = "Get all roles")
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @Operation(summary = "Delete", description = "Delete a role by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}