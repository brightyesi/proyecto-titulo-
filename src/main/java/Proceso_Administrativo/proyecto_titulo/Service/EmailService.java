package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAlertaVencimiento(String emailDestinatario, Factura factura, int diasRestantes) {

        if ("PENDIENTE".equals(remitente)) {
            log.warn("Correo no configurado. Saltando alerta para factura: {}",
                    factura.getFolio());
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(emailDestinatario);
            helper.setSubject(construirAsunto(factura.getFolio(), diasRestantes));
            helper.setText(contruirCuerpo(factura, diasRestantes), true);

            mailSender.send(mensaje);

            log.info("Alerta enviada a {} - Factura {} - {} días restantes",
                    emailDestinatario, factura.getFolio(), diasRestantes);

        } catch (MessagingException e) {
            log.error("Error al enviar alerta para factura {}: {}",
                    factura.getFolio(), e.getMessage());
        }
    }

    private  String construirAsunto(String folio, int diasRestantes) {
        if (diasRestantes <= 0) {
            return "URGENTE: Factura " + folio + " vence HOY";
        } else if (diasRestantes == 3) {
            return "AVISO: Factura " + folio + " vence en 3 días";
        } else {
            return "AVISO: Factura " + folio + " vence en " + diasRestantes + " dias";
        }
    }

    private String contruirCuerpo(Factura factura, int diasRestantes) {

        String color;
        String mensaje;

        if (diasRestantes == 0) {
            color = "#CC0000";
            mensaje = "La siguiente factura <strong>vence HOY</strong>."
                    + " Se requiere acción inmediata.";
        } else if (diasRestantes == 3) {
            color = "#FF9900";
            mensaje = "La siguiente factura vence en <strong>3 dias</strong>.";
        } else {
            color = "#2E7D32";
            mensaje = "La siguiente factura vence en <strong>"
                    + diasRestantes + "  dias</strong>.";
        }
        return "<html><body style='font-family:Arial,sans-serif;padding:20px;'>"
                + "<h2 style='color:" + color + ";'>Alerta de Vencimiento</h2>"
                + "<p>" + mensaje + "</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + "<tr style='background:#f5f5f5;'>"
                + "<td style='padding:8px;border:1px solid #ddd;'><strong>Folio</strong></td>"
                + "<td style='padding:8px;border:1px solid #ddd;'>"
                + factura.getFolio() + "</td></tr>"
                + "<tr>"
                + "<td style='padding:8px;border:1px solid #ddd;'><strong>Emisor</strong></td>"
                + "<td style='padding:8px;border:1px solid #ddd;'>"
                + factura.getEmisor() + "</td></tr>"
                + "<tr style='background:#f5f5f5;'>"
                + "<td style='padding:8px;border:1px solid #ddd;'><strong>Monto Total</strong></td>"
                + "<td style='padding:8px;border:1px solid #ddd;'>$"
                + factura.getMontoTotal() + "</td></tr>"
                + "<tr>"
                + "<td style='padding:8px;border:1px solid #ddd;'>"
                + "<strong>Fecha Vencimiento</strong></td>"
                + "<td style='padding:8px;border:1px solid #ddd;'>"
                + factura.getFechaVencimiento() + "</td></tr>"
                + "</table>"
                + "<br><p style='color:#999;font-size:11px;'>"
                + "Mensaje automatico — Sistema de Gestion de Facturas Agricolas</p>"
                + "</body></html>";
    }
}
