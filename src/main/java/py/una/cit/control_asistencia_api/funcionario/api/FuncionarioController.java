package py.una.cit.control_asistencia_api.funcionario.api;

import java.util.List;

import jakarta.validation.Valid;
import py.una.cit.control_asistencia_api.funcionario.api.dto.FuncionarioRequest;
import py.una.cit.control_asistencia_api.funcionario.api.dto.FuncionarioResponse;
import py.una.cit.control_asistencia_api.funcionario.application.FuncionarioService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<FuncionarioResponse> listar(
            @RequestParam(defaultValue = "false") boolean soloActivos) {

        return soloActivos
                ? funcionarioService.listarActivos()
                : funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public FuncionarioResponse buscarPorId(@PathVariable Long id) {
        return funcionarioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponse crear(
            @Valid @RequestBody FuncionarioRequest request) {

        return funcionarioService.crear(request);
    }

    @PutMapping("/{id}")
    public FuncionarioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioRequest request) {

        return funcionarioService.actualizar(id, request);
    }

    @PatchMapping("/{id}/activo")
    public FuncionarioResponse cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        return funcionarioService.cambiarEstado(id, request.activo());
    }

    public record CambiarEstadoRequest(Boolean activo) {
    }
}

