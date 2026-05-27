package Proceso_Administrativo.proyecto_titulo.Controller;

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
    public ResponseEntity<List<Factura>> listarTodas() {
        return new ResponseEntity<>(facturaService.obtenerTodas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity <Factura> obtenerPorId(@PathVariable Long id){
        return facturaService.obtenerPorId(id)
                .map(factura -> new ResponseEntity<>(factura, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity<Factura> actulizarFactura(@PathVariable Long id, @RequestBody Factura factura){
        try{
            Factura facturaActualuzar=facturaService.actulizarFactura(id,factura);
            return new ResponseEntity<>(facturaActualuzar,HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody Factura factura) {
        Factura nuevaFactura = facturaService.guardarFactura(factura);
        return new ResponseEntity<>(nuevaFactura, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.elilimarFactura(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/busqueda")
    public ResponseEntity<List<Factura>> buscarPorEstadoYVencimiento(
            @RequestParam EstadoFactura estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento) {

        List<Factura> facturas = facturaService.obtenerPorEstadoVencimiento(estado, fechaVencimiento);
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

}
