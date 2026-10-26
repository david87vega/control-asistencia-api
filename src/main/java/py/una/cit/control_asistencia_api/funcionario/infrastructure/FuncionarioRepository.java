package py.una.cit.control_asistencia_api.funcionario.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import py.una.cit.control_asistencia_api.funcionario.domain.Funcionario;

public interface FuncionarioRepository
        extends JpaRepository<Funcionario, Long> {

    boolean existsByCedula(String cedula);

    Optional<Funcionario> findByCedula(String cedula);

    List<Funcionario> findAllByOrderByApellidoAscNombreAsc();

    List<Funcionario> findByActivoTrueOrderByApellidoAscNombreAsc();
}

