package py.una.cit.control_asistencia_api.marcacion.infrastructure;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import py.una.cit.control_asistencia_api.marcacion.domain.Marcacion;


public interface MarcacionRepository
        extends JpaRepository<Marcacion, Long> {

    List<Marcacion> findByFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(
            OffsetDateTime inicio,
            OffsetDateTime fin);

            Optional<Marcacion> findTopByFuncionario_IdOrderByFechaHoraDesc(
            Long funcionarioId
    );
}

