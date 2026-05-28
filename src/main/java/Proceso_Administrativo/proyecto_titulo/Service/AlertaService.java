package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.Modelo.AlertConfig;
import Proceso_Administrativo.proyecto_titulo.Modelo.Alerta;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaConfigRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlertaService {
    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final FacturaRepository facturaRepository;
    private final AlertaRepository alertaRepository;
    private final AlertaConfigRepository alertaConfigRepository;
    private final EmailService emailService;

    public AlertaService(FacturaRepository facturaRepository,
                         AlertaRepository alertaRepository,
                         AlertaConfigRepository alertaConfigRepository,
                         EmailService emailService) {
        this.facturaRepository = facturaRepository;
        this.alertaRepository = alertaRepository;
        this.alertaConfigRepository = alertaConfigRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarVencimientos() {

        log.info("=== Motor de alertas iniciado: {} ===", LocalDate.now());

        List<AlertConfig> configuraciones = alertaConfigRepository.findByActivoTrue();

        if (configuraciones.isEmpty()) {
            log.warn("No hay configuraciones de alerta activas en la BD.");
            return;
        }

        for (AlertConfig config : configuraciones) {
            LocalDate fechaObjetivo = LocalDate.now().plusDays(config.getDiasPrevios());

            List<Factura> facturas = facturaRepository
                    .findByEstadoAndFechaVencimiento(EstadoFactura.PENDIENTE, fechaObjetivo);

            log.info("Revisando alertas de {} dias -> fecha objetivo: {} -> {} facturas encontradas",
                    config.getDiasPrevios(), fechaObjetivo, facturas.size());
            for (Factura factura : facturas) {
                procesarAlerta(factura, config.getDiasPrevios());
            }
        }

        log.info("=== Motor de alertas finalizado ===");
    }

    private void procesarAlerta(Factura factura, int diasPrevios) {

        boolean yaEnviada = alertaRepository.existsByFacturaAndDiasPreviosAndEnviada(
                factura, diasPrevios, true
        );
        if (yaEnviada) {
            log.info("Alerta ya enviada para factura {} ({} dias). Saltando.",
                    factura.getFolio(), diasPrevios);
            return;
        }

        String emailDestinatario = factura.getUsuario().getEmail();

        emailService.enviarAlertaVencimiento(emailDestinatario, factura, diasPrevios);

        Alerta alerta = Alerta.builder()
                .factura(factura)
                .usuario(factura.getUsuario())
                .diasPrevios(diasPrevios)
                .enviada(true)
                .fechaEnvio(LocalDate.now())
                .build();

        alertaRepository.save(alerta);

        log.info("Alerta registrada -> Factura: {} | Dias: {} | Destinatario: {}",
                factura.getFolio(), diasPrevios, emailDestinatario);
    }
}
