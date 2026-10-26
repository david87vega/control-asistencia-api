package py.una.cit.control_asistencia_api.funcionario.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.una.cit.control_asistencia_api.dependencia.domain.Dependencia;
import py.una.cit.control_asistencia_api.dependencia.infraestructure.DependenciaRepository;
import py.una.cit.control_asistencia_api.funcionario.api.dto.FuncionarioRequest;
import py.una.cit.control_asistencia_api.funcionario.api.dto.FuncionarioResponse;
import py.una.cit.control_asistencia_api.funcionario.domain.Funcionario;
import py.una.cit.control_asistencia_api.funcionario.infrastructure.FuncionarioRepository;


@Service
@Transactional
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final DependenciaRepository dependenciaRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            DependenciaRepository dependenciaRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.dependenciaRepository = dependenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponse> listarTodos() {
        return funcionarioRepository.findAllByOrderByApellidoAscNombreAsc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponse> listarActivos() {
        return funcionarioRepository.findByActivoTrueOrderByApellidoAscNombreAsc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioResponse buscarPorId(Long id) {
        return convertirAResponse(obtenerFuncionario(id));
    }

    public FuncionarioResponse crear(FuncionarioRequest request) {
        String cedula = request.cedula().trim();

        if (funcionarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "Ya existe un funcionario con la cédula " + cedula);
        }

        Dependencia dependencia = obtenerDependenciaActiva(
                request.dependenciaId());

        Funcionario funcionario = new Funcionario();
        funcionario.setCedula(cedula);
        funcionario.setNombre(request.nombre().trim());
        funcionario.setApellido(request.apellido().trim());
        funcionario.setDependencia(dependencia);
        funcionario.setActivo(true);
        funcionario.setFechaCreacion(java.time.LocalDateTime.now());

        return convertirAResponse(funcionarioRepository.save(funcionario));
    }

    public FuncionarioResponse actualizar(
            Long id,
            FuncionarioRequest request) {

        Funcionario funcionario = obtenerFuncionario(id);
        String cedula = request.cedula().trim();

        funcionarioRepository.findByCedula(cedula)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException(
                            "Ya existe otro funcionario con la cédula " + cedula);
                });

        Dependencia dependencia = obtenerDependenciaActiva(
                request.dependenciaId());

        funcionario.setCedula(cedula);
        funcionario.setNombre(request.nombre().trim());
        funcionario.setApellido(request.apellido().trim());
        funcionario.setDependencia(dependencia);

        return convertirAResponse(funcionarioRepository.save(funcionario));
    }

    public FuncionarioResponse cambiarEstado(Long id, Boolean activo) {
        if (activo == null) {
            throw new IllegalArgumentException(
                    "El estado activo es obligatorio");
        }

        Funcionario funcionario = obtenerFuncionario(id);
        funcionario.setActivo(activo);

        return convertirAResponse(funcionarioRepository.save(funcionario));
    }

    private Funcionario obtenerFuncionario(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el funcionario con ID " + id));
    }

    private Dependencia obtenerDependenciaActiva(Long id) {
        Dependencia dependencia = dependenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la dependencia con ID " + id));

        if (!Boolean.TRUE.equals(dependencia.getActivo())) {
            throw new IllegalArgumentException(
                    "La dependencia seleccionada está inactiva");
        }

        return dependencia;
    }

    private FuncionarioResponse convertirAResponse(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getCedula(),
                funcionario.getNombre(),
                funcionario.getApellido(),
                funcionario.getDependencia().getId(),
                funcionario.getDependencia().getNombre(),
                funcionario.getActivo(),
                funcionario.getFechaCreacion()
        );
    }
}

