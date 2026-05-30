package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.MensajeResponse;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorRequestDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.ProveedorResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Service.ProveedorService;
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

    // POST /api/proveedores
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProveedorRequestDTO request) {
        try {
            ProveedorResponseDTO response = proveedorService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeResponse(e.getMessage(), false));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeResponse(e.getMessage(), false));
        }
    }

    // GET /api/proveedores
    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> listar() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    // GET /api/proveedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(proveedorService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeResponse(e.getMessage(), false));
        }
    }

    // PUT /api/proveedores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ProveedorRequestDTO request) {
        try {
            return ResponseEntity.ok(proveedorService.actualizar(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeResponse(e.getMessage(), false));
        }
    }

    // DELETE /api/proveedores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            proveedorService.desactivar(id);
            return ResponseEntity.ok(new MensajeResponse("Proveedor desactivado correctamente", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeResponse(e.getMessage(), false));
        }
    }
}