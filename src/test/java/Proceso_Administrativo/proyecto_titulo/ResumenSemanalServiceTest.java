package Proceso_Administrativo.proyecto_titulo;

import Proceso_Administrativo.proyecto_titulo.Modelo.*;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import Proceso_Administrativo.proyecto_titulo.Service.EmailService;
import Proceso_Administrativo.proyecto_titulo.Service.ResumenSemanalService;
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
public class ResumenSemanalServiceTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private ResumenSemanalService resumenSemanalService;

    private User admin;
    private Factura factura;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(1L)
                .nombre("Admin Sistema")
                .email("admin@empresa.cl")
                .build();

        factura = Factura.builder()
                .id(1L)
                .folio("F-001")
                .emisor("Proveedor SPA")
                .montoTotal(new BigDecimal("150000"))
                .fechaVencimiento(LocalDate.now().plusDays(3))
                .estado(EstadoFactura.PENDIENTE)
                .eliminado(false)
                .build();
    }

    @Test
    @DisplayName("Envia resumen a los administradores cuando hay facturas por vencer")
    void enviarResumen_conFacturas() {
        when(facturaRepository.findByEstadoAndEliminadoFalseAndFechaVencimientoBetween(
                eq(EstadoFactura.PENDIENTE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(factura));
        when(userRepository.findByRol_NameRolAndActivoTrue(Roles.NombreRol.ROLE_ADMINISTRADOR))
                .thenReturn(List.of(admin));

        resumenSemanalService.enviarResumenSemanal();

        verify(emailService, times(1)).enviarResumenSemanal(eq("admin@empresa.cl"), anyList());
    }

    @Test
    @DisplayName("No envia nada si no hay facturas por vencer esta semana")
    void enviarResumen_sinFacturas() {
        when(facturaRepository.findByEstadoAndEliminadoFalseAndFechaVencimientoBetween(
                eq(EstadoFactura.PENDIENTE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        resumenSemanalService.enviarResumenSemanal();

        verify(userRepository, never()).findByRol_NameRolAndActivoTrue(any());
        verify(emailService, never()).enviarResumenSemanal(any(), anyList());
    }

    @Test
    @DisplayName("No envia nada si no hay administradores activos")
    void enviarResumen_sinAdministradores() {
        when(facturaRepository.findByEstadoAndEliminadoFalseAndFechaVencimientoBetween(
                eq(EstadoFactura.PENDIENTE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(factura));
        when(userRepository.findByRol_NameRolAndActivoTrue(Roles.NombreRol.ROLE_ADMINISTRADOR))
                .thenReturn(List.of());

        resumenSemanalService.enviarResumenSemanal();

        verify(emailService, never()).enviarResumenSemanal(any(), anyList());
    }
}