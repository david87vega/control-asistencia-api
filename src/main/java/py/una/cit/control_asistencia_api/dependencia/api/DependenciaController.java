package py.una.cit.control_asistencia_api.dependencia.api;



import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.una.cit.control_asistencia_api.dependencia.application.DependenciaService;
import py.una.cit.control_asistencia_api.dependencia.application.DependenciaService.DependenciaResponse;

@RestController
@RequestMapping("/api/dependencias")
public class DependenciaController {

    private final DependenciaService dependenciaService;

    public DependenciaController(
            DependenciaService dependenciaService) {
        this.dependenciaService = dependenciaService;
    }

    @GetMapping
    public List<DependenciaResponse> listarActivas() {
        return dependenciaService.listarActivas();
    }
}

