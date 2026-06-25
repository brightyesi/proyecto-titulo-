package Proceso_Administrativo.proyecto_titulo;


import Proceso_Administrativo.proyecto_titulo.Modelo.*;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaConfigRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Service.AlertaService;
import Proceso_Administrativo.proyecto_titulo.Service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertaServiceTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private AlertaRepository alertaRepository;
    @Mock private AlertaConfigRepository alertaConfigRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private AlertaService alertaService;

    private User usuario;
    private Factura factura;

    @BeforeEach
    void setUp(){
        usuario=User.builder()
                .id(1L)
                .nombre("Jason Nose")
                .email("jason@gmail.com")
                .build();
        factura = Factura.builder()
                .id(1L)
                .folio("F-001")
                .emisor("Proveedor SPA")
                .montoTotal(new BigDecimal("150000"))
                .estado(EstadoFactura.PENDIENTE)
                .usuario(usuario)
                .eliminado(false)
                .build();
    }

    private AlertConfig configDias(int dias){
        return AlertConfig.builder()
                .diasPrevios(dias)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Motor alecta envia correo para factura que vence en 5 dias")
    void verificarVencimiento_factura5dias(){
        AlertConfig config5 = configDias(5);
        LocalDate fechaObjetivo = LocalDate.now().plusDays(5);
        factura.setFechaVencimiento(fechaObjetivo);

        when(alertaConfigRepository.findByActivoTrue()).thenReturn(List.of(config5));
        when(facturaRepository.findByEstadoAndFechaVencimientoAndEliminadoFalse(EstadoFactura.PENDIENTE,fechaObjetivo)).thenReturn(List.of(factura));
        when(alertaRepository.existsByFacturaAndDiasPreviosAndEnviada(factura,5,true)).thenReturn(false);

        alertaService.verificarVencimientos();
        verify(emailService,times(1)).enviarAlertaVencimiento(eq("jason@gmail.com"),eq(factura),eq(5));
        verify(alertaRepository,times(1)).save(any(Alerta.class));

    }

    @Test
    @DisplayName("Motor alecta envia correo para factura que vence en 3 dias")
    void verificarVencimiento_factura3dias(){
        AlertConfig config3 = configDias(3);
        LocalDate fechaObjetivo = LocalDate.now().plusDays(3);
        factura.setFechaVencimiento(fechaObjetivo);

        when(alertaConfigRepository.findByActivoTrue()).thenReturn(List.of(config3));
        when(facturaRepository.findByEstadoAndFechaVencimientoAndEliminadoFalse(EstadoFactura.PENDIENTE, fechaObjetivo)).thenReturn(List.of(factura));
        when(alertaRepository.existsByFacturaAndDiasPreviosAndEnviada(factura,3,true)).thenReturn(false);

        alertaService.verificarVencimientos();
        verify(emailService,times(1)).enviarAlertaVencimiento(eq("jason@gmail.com"),eq(factura),eq(3));
        verify(alertaRepository,times(1)).save(any(Alerta.class));

    }

    @Test
    @DisplayName("Motor alecta envia correo para factura que vence hoy 0 dias ")
    void verificarVencimiento_factura0dias(){
        AlertConfig config3 = configDias(0);
        LocalDate fechaObjetivo = LocalDate.now().plusDays(0);
        factura.setFechaVencimiento(fechaObjetivo);

        when(alertaConfigRepository.findByActivoTrue()).thenReturn(List.of(config3));
        when(facturaRepository.findByEstadoAndFechaVencimientoAndEliminadoFalse(EstadoFactura.PENDIENTE, fechaObjetivo)).thenReturn(List.of(factura));
        when(alertaRepository.existsByFacturaAndDiasPreviosAndEnviada(factura,0,true)).thenReturn(false);

        alertaService.verificarVencimientos();
        verify(emailService,times(1)).enviarAlertaVencimiento(eq("jason@gmail.com"),eq(factura),eq(0));
        verify(alertaRepository,times(1)).save(any(Alerta.class));

    }

    @Test
    @DisplayName("No envia correo si la alerta ya fue enviada antes")
    void verificarVencimiento_alertaYaEnviada(){
        AlertConfig config5 = configDias(5);
        LocalDate fechaObjetivo = LocalDate.now().plusDays(5);
        factura.setFechaVencimiento(fechaObjetivo);

        when(alertaConfigRepository.findByActivoTrue()).thenReturn(List.of(config5));
        when(facturaRepository.findByEstadoAndFechaVencimientoAndEliminadoFalse(EstadoFactura.PENDIENTE, fechaObjetivo))
                .thenReturn(List.of(factura));
        // se simula si ya se envio
        when(alertaRepository.existsByFacturaAndDiasPreviosAndEnviada(factura,5,true)).thenReturn(true);

        alertaService.verificarVencimientos();
        //no debe mandar ni guardar nada de correo
        verify(emailService, never()).enviarAlertaVencimiento(any(), any(), anyInt());
        verify(alertaRepository,never()).save(any(Alerta.class));
    }

    @Test
    @DisplayName("No hace nada si no hay configuraciones de alerta activas")
    void verificarVencimiento_sinConfiguraciones() {
        when(alertaConfigRepository.findByActivoTrue()).thenReturn(List.of());

        alertaService.verificarVencimientos();

        verify(facturaRepository, never())
                .findByEstadoAndFechaVencimientoAndEliminadoFalse(any(), any());
        verify(emailService, never()).enviarAlertaVencimiento(any(), any(), anyInt());
    }

}
