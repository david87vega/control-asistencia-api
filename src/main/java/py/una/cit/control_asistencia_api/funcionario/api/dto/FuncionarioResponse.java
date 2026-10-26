package py.una.cit.control_asistencia_api.funcionario.api.dto;

import java.time.LocalDateTime;

public record FuncionarioResponse(
        Long id,
        String cedula,
        String nombre,
        String apellido,
        Long dependenciaId,
        String dependenciaNombre,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}

