package py.una.cit.control_asistencia_api.marcacion.api.dto;

import java.time.OffsetDateTime;

public record MarcacionDetalleResponse(
        Long id,
        OffsetDateTime fechaHora,
        String tipo,
        String origen
) {
}

