package py.una.cit.control_asistencia_api.funcionario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FuncionarioRequest(

        @NotBlank(message = "La cédula es obligatoria")
        @Size(max = 20, message = "La cédula no puede superar 20 caracteres")
        String cedula,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
        String apellido,

        @NotNull(message = "La dependencia es obligatoria")
        Long dependenciaId

) {
}

