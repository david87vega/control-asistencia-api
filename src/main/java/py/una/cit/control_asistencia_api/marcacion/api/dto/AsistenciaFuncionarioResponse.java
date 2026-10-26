package py.una.cit.control_asistencia_api.marcacion.api.dto;

import java.util.List;

public record AsistenciaFuncionarioResponse(
        Long funcionarioId,
        String cedula,
        String nombre,
        String apellido,
        Long dependenciaId,
        String dependenciaNombre,
        List<MarcacionDetalleResponse> marcaciones
) {
}

