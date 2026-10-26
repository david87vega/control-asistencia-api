package py.una.cit.control_asistencia_api.dependencia.infraestructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.una.cit.control_asistencia_api.dependencia.domain.Dependencia;

public interface DependenciaRepository
        extends JpaRepository<Dependencia, Long> {

    List<Dependencia> findByActivoTrueOrderByNombreAsc();
}

