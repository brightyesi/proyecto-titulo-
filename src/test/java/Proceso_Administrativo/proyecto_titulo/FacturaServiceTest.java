package Proceso_Administrativo.proyecto_titulo;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.Modelo.*;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.HistorialEstadoRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import Proceso_Administrativo.proyecto_titulo.Service.AlmacenamientoService;
import Proceso_Administrativo.proyecto_titulo.Service.FacturaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock private FacturaRepository facturaRepository;
    @Mock private UserRepository userRepository;
    @Mock private HistorialEstadoRepository historialEstadoRepository;
    @Mock private AlmacenamientoService almacenamientoService;

    @InjectMocks
    private FacturaService facturaService;

    private User usuario;
    private Factura factura;

    @BeforeEach
    void setUp() {
        usuario = User.builder()
                .id(1L)
                .nombre("Jason Nose")
                .email("jason@gmail.com")
                .build();

        factura = Factura.builder()
                .id(1L)
                .folio("F-001")
                .emisor("Proveedor SPA")
                .montoTotal(new BigDecimal("150000"))
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(10))
                .estado(EstadoFactura.PENDIENTE)
                .usuario(usuario)
                .eliminado(false)
                .build();
    }

    // soft delete
    @Test
    @DisplayName("Eliminar factura la marca como eliminada (soft delete)")
    void eliminarFactura_soloMarcaComoEliminada() {
        when(facturaRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.of(factura));

        facturaService.eliminarFactura(1L);

        assertTrue(factura.isEliminado());                 // se marcó
        assertNotNull(factura.getFechaEliminacion());      // se anotó la fecha
        verify(facturaRepository, times(1)).save(factura); // se guardó
    }

    @Test
    @DisplayName("Eliminar factura inexistente lanza excepcion")
    void eliminarFactura_noEncontrada() {
        when(facturaRepository.findByIdAndEliminadoFalse(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> facturaService.eliminarFactura(99L));
        verify(facturaRepository, never()).save(any());
    }

    // restaurar
    @Test
    @DisplayName("Restaurar saca la factura de la papelera")
    void restaurarFactura_exito() {
        factura.setEliminado(true);
        factura.setFechaEliminacion(LocalDate.now());
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(factura)).thenReturn(factura);

        facturaService.restaurarFactura(1L);

        assertFalse(factura.isEliminado());             // ya no eliminada
        assertNull(factura.getFechaEliminacion());      // fecha limpiada
    }

    @Test
    @DisplayName("Restaurar una factura que NO esta en papelera lanza excepcion")
    void restaurarFactura_noEstaEnPapelera() {
        factura.setEliminado(false);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        assertThrows(RuntimeException.class, () -> facturaService.restaurarFactura(1L));
        verify(facturaRepository, never()).save(any());
    }

    // actualizar + el hisotrial
    @Test
    @DisplayName("Actualizar con cambio de estado registra el historial")
    void actualizarFactura_cambioEstado_registraHistorial() {
        FacturaResquestDto dto = new FacturaResquestDto();
        dto.setFolio("F-001");
        dto.setEmisor("Proveedor SPA");
        dto.setMontoTotal(new BigDecimal("150000"));
        dto.setFechaEmision(LocalDate.now());
        dto.setFechaVencimiento(LocalDate.now().plusDays(10));
        dto.setEstado(EstadoFactura.PAGADA);   // cambia de PENDIENTE a PAGADA
        dto.setUsuarioId(1L);

        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(facturaRepository.save(factura)).thenReturn(factura);

        facturaService.actualizarFactura(1L, dto);

        verify(historialEstadoRepository, times(1)).save(any(HistorialEstado.class));
    }

    @Test
    @DisplayName("Actualizar sin cambio de estado NO registra historial")
    void actualizarFactura_mismoEstado_noRegistraHistorial() {
        FacturaResquestDto dto = new FacturaResquestDto();
        dto.setFolio("F-001");
        dto.setEmisor("Proveedor SPA");
        dto.setMontoTotal(new BigDecimal("150000"));
        dto.setFechaEmision(LocalDate.now());
        dto.setFechaVencimiento(LocalDate.now().plusDays(10));
        dto.setEstado(EstadoFactura.PENDIENTE); // mismo estado
        dto.setUsuarioId(1L);

        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(facturaRepository.save(factura)).thenReturn(factura);

        facturaService.actualizarFactura(1L, dto);

        verify(historialEstadoRepository, never()).save(any(HistorialEstado.class));
    }

    // obtiene por el id
    @Test
    @DisplayName("Obtener por id inexistente lanza excepcion")
    void obtenerPorId_noEncontrada() {
        when(facturaRepository.findByIdAndEliminadoFalse(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> facturaService.obtenerPorId(99L));
    }
}