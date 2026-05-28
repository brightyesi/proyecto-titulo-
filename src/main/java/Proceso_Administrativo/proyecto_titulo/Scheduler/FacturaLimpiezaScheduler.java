package Proceso_Administrativo.proyecto_titulo.Scheduler;

import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FacturaLimpiezaScheduler {

    private final FacturaRepository facturaRepository;

    @Autowired
    public FacturaLimpiezaScheduler(FacturaRepository facturaRepository){
        this.facturaRepository=facturaRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void limpiarPapeleraFacturas() {
        // Calculamos la fecha de hace 30 días
        LocalDate fechaLimite = LocalDate.now().minusDays(30);

        // Buscamos las facturas cuya fechaEliminacion sea ANTERIOR a esa fecha límite
        List<Factura> facturasParaBorrarDefinitivamente =
                facturaRepository.findByEliminadoTrueAndFechaEliminacionBefore(fechaLimite);

        if (!facturasParaBorrarDefinitivamente.isEmpty()) {
            // Aquí sí se hace el borrado físico y definitivo de la Base de Datos
            facturaRepository.deleteAll(facturasParaBorrarDefinitivamente);
            System.out.println("Se eliminaron definitivamente " + facturasParaBorrarDefinitivamente.size() + " facturas viejas de la papelera.");
        }
    }

}
