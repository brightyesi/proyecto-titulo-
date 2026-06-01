package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResumenSemanalService {
    private static final Logger log =  LoggerFactory.getLogger(ResumenSemanalService.class);

    private final FacturaRepository facturaRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public  ResumenSemanalService( FacturaRepository facturaRepository,
                                   UserRepository userRepository,
                                   EmailService emailService) {
        this.facturaRepository = facturaRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * MON")
    public void enviarResumenSemanal() {

        log.info("=== Resumen semanal iniciado: {} ===", LocalDate.now());

        LocalDate hoy = LocalDate.now();
        LocalDate enUnaSemana = hoy.plusDays(7);

        List<Factura> facturas = facturaRepository
                .findByEstadoAndEliminadoFalseAndFechaVencimientoBetween(
                        EstadoFactura.PENDIENTE, hoy, enUnaSemana);

        if (facturas.isEmpty()) {
            log.info("No hay facturas por vencer esta semana. No se envia resumen.");
            return;
        }

        List<User> administradores = userRepository
                .findByRol_NameRolAndActivoTrue(Roles.NombreRol.ROLE_ADMINISTRADOR);

        if (administradores.isEmpty()) {
            log.warn("No hay administradores activos para enviar el resumen.");
            return;
        }

        for (User admin : administradores) {
            emailService.enviarResumenSemanal(admin.getEmail(), facturas);
        }

        log.info("=== Resumen semanal enviado a {} administradores ({} facturas) ===",
                administradores.size(), facturas.size());
    }
}
