package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResponseDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.DTO.HistorialEstadoResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Service.FacturaService;
import Proceso_Administrativo.proyecto_titulo.Service.HistorialEstadoService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final HistorialEstadoService historialEstadoService;

    public FacturaController(FacturaService facturaService, HistorialEstadoService historialEstadoService) {
        this.facturaService = facturaService;
        this.historialEstadoService = historialEstadoService;
    }

    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@Valid @RequestBody FacturaResquestDto dto) {
        return new ResponseEntity<>(facturaService.guardarFactura(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> actualizarFactura(@PathVariable Long id,
                                                                @Valid @RequestBody FacturaResquestDto dto) {
        return ResponseEntity.ok(facturaService.actualizarFactura(id, dto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/busqueda")
    public ResponseEntity<List<FacturaResponseDTO>> buscarPorEstadoYVencimiento(
            @RequestParam EstadoFactura estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento) {
        return ResponseEntity.ok(facturaService.obtenerPorEstadoVencimiento(estado, fechaVencimiento));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<FacturaResponseDTO> restaurarFactura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.restaurarFactura(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/papelera")
    public ResponseEntity<List<FacturaResponseDTO>> verPapelera() {
        return ResponseEntity.ok(facturaService.obtenerEliminadas());
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponseDTO>> verHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(historialEstadoService.obtenerHistorialPorFactura(id));
    }

    @PostMapping("/{id}/documento")
    public ResponseEntity<FacturaResponseDTO> subirDocumento(@PathVariable Long id,
                                                             @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(facturaService.adjuntarDocumento(id, archivo));
    }

    @GetMapping("/{id}/documento")
    public ResponseEntity<Resource> verDocumento(@PathVariable Long id) {
        Resource recurso = facturaService.obtenerDocumento(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}