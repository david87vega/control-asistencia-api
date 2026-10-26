package py.una.cit.control_asistencia_api.marcacion.application;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import py.una.cit.control_asistencia_api.funcionario.domain.Funcionario;
import py.una.cit.control_asistencia_api.funcionario.infrastructure.FuncionarioRepository;
import py.una.cit.control_asistencia_api.marcacion.api.dto.MarcacionRegistroResponse;
import py.una.cit.control_asistencia_api.marcacion.api.dto.MarcacionRequest;
import py.una.cit.control_asistencia_api.marcacion.domain.Marcacion;
import py.una.cit.control_asistencia_api.marcacion.infrastructure.MarcacionRepository;

@Service
public class MarcacionRegistroService {

    private static final ZoneId ZONA_PARAGUAY = ZoneId.of("America/Asuncion");

    private final MarcacionRepository marcacionRepository;
    private final FuncionarioRepository funcionarioRepository;

    public MarcacionRegistroService(
            MarcacionRepository marcacionRepository,
            FuncionarioRepository funcionarioRepository) {
        this.marcacionRepository = marcacionRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public MarcacionRegistroResponse registrar(
            MarcacionRequest request) {

        Funcionario funcionario = funcionarioRepository
                .findById(request.funcionarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el funcionario seleccionado"));

        if (!Boolean.TRUE.equals(funcionario.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar la marcación de un funcionario inactivo");
        }

        String tipo = request.tipo();

        var ultimaMarcacion = marcacionRepository
                .findTopByFuncionario_IdOrderByFechaHoraDesc(
                        funcionario.getId());

        if (ultimaMarcacion.isPresent()) {
            String ultimoTipo = ultimaMarcacion.get().getTipo();

            if (ultimoTipo.equals(tipo)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El funcionario ya tiene una última marcación de tipo "
                                + tipo);
            }

            if ("SALIDA".equals(tipo)
                    && !"ENTRADA".equals(ultimoTipo)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No se puede registrar una salida sin una entrada previa");
            }
        } else if ("SALIDA".equals(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede registrar una salida sin una entrada previa");
        }

        Marcacion marcacion = new Marcacion();

        marcacion.setFuncionario(funcionario);
        marcacion.setTipo(tipo);
        marcacion.setOrigen("MANUAL");

        // Fecha y hora en que ocurrió la marcación.
        OffsetDateTime ahora = OffsetDateTime.now(ZONA_PARAGUAY);
        marcacion.setFechaHora(ahora);

        // Fecha y hora en que se creó el registro en la base de datos.
        // Es obligatorio porque la columna fecha_creacion no acepta NULL.
        marcacion.setFechaCreacion(ahora);

        Marcacion guardada = marcacionRepository.save(marcacion);

        return new MarcacionRegistroResponse(
                guardada.getId(),
                funcionario.getId(),
                funcionario.getNombre(),
                funcionario.getApellido(),
                guardada.getTipo(),
                guardada.getFechaHora(),
                guardada.getOrigen());
    }
}
