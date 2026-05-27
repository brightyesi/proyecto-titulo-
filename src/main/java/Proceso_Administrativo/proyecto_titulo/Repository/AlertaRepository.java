package Proceso_Administrativo.proyecto_titulo.Repository;

import Proceso_Administrativo.proyecto_titulo.Modelo.Alerta;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    boolean existsByFacturaAndDiasPreviosAndEnviada(
            Factura factura,
            int diasPrevios,
            boolean enviada
    );
}
