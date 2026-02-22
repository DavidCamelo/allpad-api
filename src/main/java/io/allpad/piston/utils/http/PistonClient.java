package io.allpad.piston.utils.http;

import io.allpad.piston.dto.ExecuteDTO;
import io.allpad.piston.dto.PackageDTO;
import io.allpad.piston.dto.PackageResponseDTO;
import io.allpad.piston.dto.RuntimeDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("api/v2")
public interface PistonClient {
    @GetExchange("runtimes")
    List<RuntimeDTO> getRuntimes();
    @PostExchange("execute")
    Object execute(@RequestBody ExecuteDTO executeDTO);
    @GetExchange("packages")
    List<PackageResponseDTO> getPackages();
    @PostExchange("packages")
    PackageDTO installPackage(@RequestBody PackageDTO packageDTO);
    @DeleteExchange("packages")
    PackageDTO uninstallPackage(@RequestBody PackageDTO packageDTO);
}
