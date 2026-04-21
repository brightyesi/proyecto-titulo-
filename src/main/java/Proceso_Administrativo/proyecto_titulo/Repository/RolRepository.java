package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Role,Long> {

    Optional<Roles>finByName(Roles.NombreRol name);

}
