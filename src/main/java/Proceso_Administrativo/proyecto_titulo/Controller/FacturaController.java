package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResponseDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.DTO.MensajeResponse;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.DTO.HistorialEstadoResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Service.HistorialEstadoService;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final HistorialEstadoService historialEstadoService;

    @Autowired
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
        try {
            return ResponseEntity.ok(facturaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@RequestBody FacturaResquestDto facturaRequestDTO) {
        try {
            FacturaResponseDTO nuevaFactura = facturaService.guardarFactura(facturaRequestDTO);
            return new ResponseEntity<>(nuevaFactura, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/documento")
    public ResponseEntity<?> subirDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            FacturaResponseDTO factura = facturaService.adjuntarDocumento(id, archivo);
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> actualizarFactura(
            @PathVariable Long id,
            @RequestBody FacturaResquestDto facturaRequestDTO) {
        try {
            FacturaResponseDTO facturaActualizada = facturaService.actualizarFactura(id, facturaRequestDTO);
            return ResponseEntity.ok(facturaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/busqueda")
    public ResponseEntity<List<FacturaResponseDTO>> buscarPorEstadoYVencimiento(
            @RequestParam EstadoFactura estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento) {

        List<FacturaResponseDTO> facturas = facturaService.obtenerPorEstadoVencimiento(estado, fechaVencimiento);
        return ResponseEntity.ok(facturas);
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<FacturaResponseDTO> restaurarFactura (@PathVariable Long id){
        try{
            FacturaResponseDTO facturaRestaurada= facturaService.restaurarFactura(id);
            return ResponseEntity.ok(facturaRestaurada);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/papelera")
    public ResponseEntity<List<FacturaResponseDTO>> verPapelera(){
        return ResponseEntity.ok(facturaService.obtenerEliminadas());
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponseDTO>> verHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(historialEstadoService.obtenerHistorialPorFactura(id));
    }

    @GetMapping("{id}/documento")
    public ResponseEntity<Resource> verDocumento(@PathVariable Long id) {
        Resource recurso = facturaService.obtenerDocumento(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}
