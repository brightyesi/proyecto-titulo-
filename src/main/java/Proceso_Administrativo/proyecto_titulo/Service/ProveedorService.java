package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorRequestDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Modelo.Proveedor;
import Proceso_Administrativo.proyecto_titulo.Repository.ProveedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    // Crear proveedor
    @Transactional
    public ProveedorResponseDTO crear(ProveedorRequestDTO request) {
        if (proveedorRepository.existsByRutProveedor(request.getRutProveedor())) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con el RUT: " + request.getRutProveedor()
            );
        }

        Proveedor proveedor = Proveedor.builder()
                .rutProveedor(request.getRutProveedor())
                .nombreComercial(request.getNombreComercial())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .activo(true)
                .build();

        return toResponse(proveedorRepository.save(proveedor));
    }

    // Listar todos los activos
    @Transactional
    public List<ProveedorResponseDTO> listar() {
        return proveedorRepository.findAll()
                .stream()
                .filter(Proveedor::isActivo)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener por ID
    @Transactional
    public ProveedorResponseDTO obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        return toResponse(proveedor);
    }

    // Actualizar
    @Transactional
    public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setNombreComercial(request.getNombreComercial());
        proveedor.setEmail(request.getEmail());
        proveedor.setTelefono(request.getTelefono());

        return toResponse(proveedorRepository.save(proveedor));
    }

    // Desactivar (soft delete)
    @Transactional
    public void desactivar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    // Helper
    private ProveedorResponseDTO toResponse(Proveedor p) {
        return ProveedorResponseDTO.builder()
                .id(p.getId())
                .rutProveedor(p.getRutProveedor())
                .nombreComercial(p.getNombreComercial())
                .email(p.getEmail())
                .telefono(p.getTelefono())
                .activo(p.isActivo())
                .build();
    }
}