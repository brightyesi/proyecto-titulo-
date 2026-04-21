package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository  extends JpaRepository<User,Long> {

    Optional<User> findByCorreo(String correo);

    Optional<User> findByNombre(String nombre);

    Boolean existsByCorreo (String correo);

    Boolean existsByNombre (String nombre);

}
