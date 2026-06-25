package Proceso_Administrativo.proyecto_titulo;

import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorRequestDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Modelo.Proveedor;
import Proceso_Administrativo.proyecto_titulo.Repository.ProveedorRepository;
import Proceso_Administrativo.proyecto_titulo.Service.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProveedorServiceTest {

    @Mock private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private ProveedorRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new ProveedorRequestDTO();
        request.setRutProveedor("11111111-1");
        request.setNombreComercial("Agricola");
        request.setEmail("contacto@agricola.cl");
        request.setTelefono("999999999");
    }

    @Test
    @DisplayName("Crear proveedor con RUT duplicado lanza excepcion")
    void crear_rutDuplicado() {
        when(proveedorRepository.existsByRutProveedor("11111111-1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> proveedorService.crear(request));
        verify(proveedorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear proveedor exitoso")
    void crear_exito() {
        when(proveedorRepository.existsByRutProveedor("11111111-1")).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        ProveedorResponseDTO response = proveedorService.crear(request);

        assertNotNull(response);
        assertEquals("11111111-1", response.getRutProveedor());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Obtener proveedor inexistente lanza excepcion")
    void obtenerPorId_noEncontrado() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> proveedorService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Desactivar proveedor lo marca como inactivo")
    void desactivar_exito() {
        Proveedor proveedor = Proveedor.builder()
                .id(1L)
                .rutProveedor("11111111-1")
                .nombreComercial("Agricola")
                .activo(true)
                .build();
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        proveedorService.desactivar(1L);

        assertFalse(proveedor.isActivo());
        verify(proveedorRepository, times(1)).save(proveedor);
    }
}