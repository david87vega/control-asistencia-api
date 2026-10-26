package py.una.cit.control_asistencia_api.dependencia.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.una.cit.control_asistencia_api.dependencia.infraestructure.DependenciaRepository;

@Service
public class DependenciaService {

    private final DependenciaRepository dependenciaRepository;

    public DependenciaService(
            DependenciaRepository dependenciaRepository) {
        this.dependenciaRepository = dependenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<DependenciaResponse> listarActivas() {
        return dependenciaRepository
                .findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(dependencia -> new DependenciaResponse(
                        dependencia.getId(),
                        dependencia.getNombre()
                ))
                .toList();
    }

    public record DependenciaResponse(
            Long id,
            String nombre
    ) {
    }
}
