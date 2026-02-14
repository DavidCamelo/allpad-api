package io.allpad.auth.api;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User API")
@RestController
@RequestMapping(value = "/api/{version}/users", version = "v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all", description = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @Operation(summary = "Assign role to user", description = "Assign role to user")
    @PostMapping("/{username}/roles/{roleName}")
    public ResponseEntity<UserDTO> assignRoleToUser(@PathVariable String username, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.assignRoleToUser(username, roleName));
    }

    @Operation(summary = "Remove role from user", description = "Remove role from user")
    @DeleteMapping("/{username}/roles/{roleName}")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.removeRoleFromUser(username, roleName));
    }
}
