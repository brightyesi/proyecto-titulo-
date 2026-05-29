package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRutProveedor(String rutProveedor);

    boolean existsByRutProveedor(String rutProveedor);
}