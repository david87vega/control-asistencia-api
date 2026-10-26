
package py.una.cit.control_asistencia_api.marcacion.application;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.una.cit.control_asistencia_api.funcionario.domain.Funcionario;
import py.una.cit.control_asistencia_api.funcionario.infrastructure.FuncionarioRepository;
import py.una.cit.control_asistencia_api.marcacion.api.dto.AsistenciaFuncionarioResponse;
import py.una.cit.control_asistencia_api.marcacion.api.dto.MarcacionDetalleResponse;
import py.una.cit.control_asistencia_api.marcacion.domain.Marcacion;
import py.una.cit.control_asistencia_api.marcacion.infrastructure.MarcacionRepository;

@Service
public class MarcacionService {

    private static final ZoneId ZONA_PARAGUAY =
            ZoneId.of("America/Asuncion");

    private final MarcacionRepository marcacionRepository;
    private final FuncionarioRepository funcionarioRepository;

    public MarcacionService(
            MarcacionRepository marcacionRepository,
            FuncionarioRepository funcionarioRepository) {
        this.marcacionRepository = marcacionRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Consulta diaria existente.
     * Se conserva para no afectar la pantalla de marcaciones.
     */
    @Transactional(readOnly = true)
    public List<AsistenciaFuncionarioResponse> consultarPorFecha(
            LocalDate fecha) {

        return consultarPorPeriodo(fecha, fecha, null, null);
    }

    /**
     * Consulta las marcaciones de un período.
     *
     * @param desde          fecha inicial, inclusive
     * @param hasta          fecha final, inclusive
     * @param dependenciaId filtro opcional por dependencia
     * @param funcionarioId  filtro opcional por funcionario
     */
    @Transactional(readOnly = true)
    public List<AsistenciaFuncionarioResponse> consultarPorPeriodo(
            LocalDate desde,
            LocalDate hasta,
            Long dependenciaId,
            Long funcionarioId) {

        OffsetDateTime inicio = desde
                .atStartOfDay(ZONA_PARAGUAY)
                .toOffsetDateTime();

        // El límite final es exclusivo para incluir todo el último día.
        OffsetDateTime fin = hasta
                .plusDays(1)
                .atStartOfDay(ZONA_PARAGUAY)
                .toOffsetDateTime();

        List<Marcacion> marcaciones =
                marcacionRepository
                        .findByFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(
                                inicio, fin);

        Map<Long, List<MarcacionDetalleResponse>> marcacionesPorFuncionario =
                marcaciones.stream()
                        .collect(Collectors.groupingBy(
                                m -> m.getFuncionario().getId(),
                                Collectors.mapping(
                                        this::convertirMarcacion,
                                        Collectors.toList()
                                )
                        ));

        List<Funcionario> funcionarios =
                funcionarioRepository
                        .findByActivoTrueOrderByApellidoAscNombreAsc();

        return funcionarios.stream()
                .filter(funcionario ->
                        dependenciaId == null
                                || funcionario.getDependencia().getId()
                                        .equals(dependenciaId))
                .filter(funcionario ->
                        funcionarioId == null
                                || funcionario.getId().equals(funcionarioId))
                .map(funcionario -> new AsistenciaFuncionarioResponse(
                        funcionario.getId(),
                        funcionario.getCedula(),
                        funcionario.getNombre(),
                        funcionario.getApellido(),
                        funcionario.getDependencia().getId(),
                        funcionario.getDependencia().getNombre(),
                        marcacionesPorFuncionario.getOrDefault(
                                funcionario.getId(),
                                List.of()
                        )
                ))
                .toList();
    }

    private MarcacionDetalleResponse convertirMarcacion(
            Marcacion marcacion) {

        return new MarcacionDetalleResponse(
                marcacion.getId(),
                marcacion.getFechaHora(),
                marcacion.getTipo(),
                marcacion.getOrigen()
        );
    }
}
