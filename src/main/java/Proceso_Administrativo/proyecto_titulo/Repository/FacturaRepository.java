package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura,Long> {

    List<Factura> findByEstadoAndFechaVencimiento(
            EstadoFactura estado,
            LocalDate fechaVencimiento
    );

    List<Factura>findByEliminadoFalse();
    Optional<Factura>findByIdAndEliminadoFalse(Long id);

    // para la papelera
    List<Factura> findByEliminadoTrue();

    // motor de  alerta, no se consideran las ya eliminadas
    List<Factura> findByEstadoAndFechaVencimientoAndEliminadoFalse(
            EstadoFactura estado,
            LocalDate fechaVencimiento
    );

    List<Factura> findByEliminadoTrueAndFechaEliminacionBefore(
            LocalDate fechaLimite
    );

    List<Factura> findByEstadoAndEliminadoFalseAndFechaVencimientoBetween(
            EstadoFactura estado,
            LocalDate inicio,
            LocalDate fin
    );
}
