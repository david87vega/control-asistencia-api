package py.una.cit.control_asistencia_api.marcacion.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record MarcacionRequest(

        @NotNull(message = "El funcionario es obligatorio")
        @Positive(message = "El ID del funcionario debe ser positivo")
        Long funcionarioId,

        @NotNull(message = "El tipo de marcación es obligatorio")
        @Pattern(
                regexp = "ENTRADA|SALIDA",
                message = "El tipo debe ser ENTRADA o SALIDA"
        )
        String tipo

) {
}
