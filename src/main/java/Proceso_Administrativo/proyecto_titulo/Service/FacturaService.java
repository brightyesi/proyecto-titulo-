package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    @Autowired
    public FacturaService (FacturaRepository facturaRepository){
        this.facturaRepository=facturaRepository;
    }

    public List<Factura> obtenerTodas(){
        return facturaRepository.findAll();
    }

    public Optional<Factura> obtenerPorId(Long id){
        return facturaRepository.findById(id);
    }

    public Factura actulizarFactura(Long id, Factura facturaActualizada){
        return facturaRepository.findById(id).map(factura -> {
            factura.setFolio(facturaActualizada.getFolio());
            factura.setEmisor(facturaActualizada.getEmisor());
            factura.setMontoTotal(facturaActualizada.getMontoTotal());
            factura.setFechaEmision(facturaActualizada.getFechaEmision());
            factura.setFechaVencimiento(facturaActualizada.getFechaVencimiento());
            factura.setEstado(facturaActualizada.getEstado());
            factura.setUsuario(facturaActualizada.getUsuario());
            return facturaRepository.save(factura);
        }).orElseThrow(()-> new RuntimeException("Factura no encontrada con id " + id));
    }
    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

    public void elilimarFactura(Long id ){
        facturaRepository.deleteById(id);
    }

    public List<Factura> obtenerPorEstadoVencimiento(EstadoFactura estado, LocalDate fechaVencimiento){
        return facturaRepository.findByEstadoAndFechaVencimiento(estado,fechaVencimiento);
    }

}
