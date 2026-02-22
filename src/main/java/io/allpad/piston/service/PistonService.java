package io.allpad.piston.service;

import io.allpad.piston.dto.ExecuteDTO;
import io.allpad.piston.dto.PackageDTO;
import io.allpad.piston.dto.RuntimeDTO;

import java.util.List;

public interface PistonService {
    List<RuntimeDTO> getRuntimes();

    Object execute(ExecuteDTO executeDTO);

    List<PackageDTO> getPackages(Boolean installed);

    PackageDTO installPackage(PackageDTO packageDTO);

    PackageDTO uninstallPackage(PackageDTO packageDTO);
}
