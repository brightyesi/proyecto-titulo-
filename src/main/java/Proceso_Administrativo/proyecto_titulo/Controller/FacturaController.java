package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResponseDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Service.FacturaService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    @Autowired
    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
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

}
