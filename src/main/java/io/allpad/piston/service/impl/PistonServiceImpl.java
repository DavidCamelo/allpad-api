package io.allpad.piston.service.impl;

import io.allpad.piston.dto.ExecuteDTO;
import io.allpad.piston.dto.PackageDTO;
import io.allpad.piston.dto.PackageRequestDTO;
import io.allpad.piston.dto.RuntimeDTO;
import io.allpad.piston.service.PistonService;
import io.allpad.piston.utils.http.PistonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PistonServiceImpl implements PistonService {
    private final PistonClient pistonClient;

    @Override
    public List<RuntimeDTO> getRuntimes() {
        return pistonClient.getRuntimes();
    }

    @Override
    public Object execute(ExecuteDTO executeDTO) {
        return pistonClient.execute(executeDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public List<PackageDTO> getPackages(Boolean installed) {
        if (installed == null) {
            return pistonClient.getPackages().stream()
                    .map(packageResponseDTO -> PackageDTO.builder()
                            .language(packageResponseDTO.language())
                            .version(packageResponseDTO.language_version())
                            .installed(packageResponseDTO.installed())
                            .build())
                    .toList();
        }
        return pistonClient.getPackages().stream()
                .filter(packageResponseDTO -> packageResponseDTO.installed().equals(installed))
                .map(packageResponseDTO -> PackageDTO.builder()
                        .language(packageResponseDTO.language())
                        .version(packageResponseDTO.language_version())
                        .installed(packageResponseDTO.installed())
                        .build())
                .toList();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public PackageDTO installPackage(PackageRequestDTO packageRequestDTO) {
        return pistonClient.installPackage(packageRequestDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public PackageDTO uninstallPackage(PackageRequestDTO packageRequestDTO) {
        return pistonClient.uninstallPackage(packageRequestDTO);
    }
}
