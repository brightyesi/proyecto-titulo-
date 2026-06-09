package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.MensajeResponse;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorRequestDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Service.ProveedorService;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {

        this.proveedorService = proveedorService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crear(@Valid @RequestBody ProveedorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> listar() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ProveedorRequestDTO request) {
        return ResponseEntity.ok(proveedorService.actualizar(id,request));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> desactivar(@PathVariable Long id) {
        proveedorService.desactivar(id);
        return ResponseEntity.ok(new MensajeResponse("Proveedor desactivado correctamente", true));
    }
}