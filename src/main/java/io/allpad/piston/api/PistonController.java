package io.allpad.piston.api;

import io.allpad.piston.dto.ExecuteDTO;
import io.allpad.piston.dto.PackageDTO;
import io.allpad.piston.dto.PackageRequestDTO;
import io.allpad.piston.dto.RuntimeDTO;
import io.allpad.piston.service.PistonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Piston API")
@RestController
@RequestMapping(value = "api/{version}/piston", version = "v2")
@RequiredArgsConstructor
public class PistonController {
    private final PistonService pistonService;

    @Operation(summary = "Get runtimes", description = "Get piston runtimes installed")
    @GetMapping("runtimes")
    public ResponseEntity<List<RuntimeDTO>> getRuntimes() {
        return ResponseEntity.ok(pistonService.getRuntimes());
    }

    @Operation(summary = "Run code", description = "Execute code in piston API")
    @PostMapping("execute")
    public ResponseEntity<Object> getExecute(@RequestBody ExecuteDTO executeDTO) {
        return ResponseEntity.ok(pistonService.execute(executeDTO));
    }

    @Operation(summary = "Get packages", description = "Get piston packages installed")
    @GetMapping("packages")
    public ResponseEntity<List<PackageDTO>> getPackages(@RequestParam(required = false) Boolean installed) {
        return ResponseEntity.ok(pistonService.getPackages(installed));
    }

    @Operation(summary = "Install package", description = "Install new runtime package")
    @PostMapping("packages")
    public ResponseEntity<PackageDTO> installPackage(@RequestBody PackageRequestDTO packageRequestDTO) {
        return ResponseEntity.ok(pistonService.installPackage(packageRequestDTO));
    }

    @Operation(summary = "Uninstall package", description = "Uninstall runtime package")
    @DeleteMapping("packages")
    public ResponseEntity<PackageDTO> uninstallPackage(@RequestBody PackageRequestDTO packageRequestDTO) {
        return ResponseEntity.ok(pistonService.uninstallPackage(packageRequestDTO));
    }
}
