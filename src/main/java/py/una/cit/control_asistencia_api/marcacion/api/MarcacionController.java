
package py.una.cit.control_asistencia_api.marcacion.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import py.una.cit.control_asistencia_api.marcacion.api.dto.AsistenciaFuncionarioResponse;
import py.una.cit.control_asistencia_api.marcacion.api.dto.MarcacionRegistroResponse;
import py.una.cit.control_asistencia_api.marcacion.api.dto.MarcacionRequest;
import py.una.cit.control_asistencia_api.marcacion.application.MarcacionRegistroService;
import py.una.cit.control_asistencia_api.marcacion.application.MarcacionService;

@RestController
@RequestMapping("/api/marcaciones")
public class MarcacionController {

    private final MarcacionService marcacionService;

    private final MarcacionRegistroService marcacionRegistroService;

    public MarcacionController(
            MarcacionService marcacionService,
            MarcacionRegistroService marcacionRegistroService) {
        this.marcacionService = marcacionService;
        this.marcacionRegistroService = marcacionRegistroService;
    }

    /**
     * Consulta diaria existente.
     */
    @GetMapping
    public List<AsistenciaFuncionarioResponse> consultarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        return marcacionService.consultarPorFecha(fecha);
    }

    /**
     * Reporte de asistencia por período.
     *
     * Ejemplo:
     * /api/marcaciones/reporte?desde=2026-10-01&hasta=2026-10-19
     */
    @GetMapping("/reporte")
    public List<AsistenciaFuncionarioResponse> consultarReporte(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(required = false)
            Long dependenciaId,

            @RequestParam(required = false)
            Long funcionarioId) {

        if (desde.isAfter(hasta)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha desde no puede ser posterior a la fecha hasta."
            );
        }

        return marcacionService.consultarPorPeriodo(
                desde,
                hasta,
                dependenciaId,
                funcionarioId
        );
    }

    /**
     * Registro de marcación existente.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarcacionRegistroResponse registrar(
            @Valid @RequestBody MarcacionRequest request) {

        return marcacionRegistroService.registrar(request);
    }
}
