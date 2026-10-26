package py.una.cit.control_asistencia_api.marcacion.api.dto;

import java.time.OffsetDateTime;

public record MarcacionRegistroResponse(
        Long id,
        Long funcionarioId,
        String nombreFuncionario,
        String apellidoFuncionario,
        String tipo,
        OffsetDateTime fechaHora,
        String origen
) {
}
