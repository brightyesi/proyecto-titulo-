package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResponseDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UserRepository userRepository;

    @Autowired
    public FacturaService (FacturaRepository facturaRepository, UserRepository userRepository){
        this.facturaRepository=facturaRepository;
        this.userRepository=userRepository;
    }
    private FacturaResponseDTO convertirAResponseDTO(Factura factura) {
        return FacturaResponseDTO.builder()
                .id(factura.getId())
                .folio(factura.getFolio())
                .emisor(factura.getEmisor())
                .montoTotal(factura.getMontoTotal())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento()) // Agregado
                .estado(factura.getEstado())
                .usuarioId(factura.getUsuario().getId())
                .usuarioNombre(factura.getUsuario().getUsername()) // Cambia 'getUsername()' por el campo real de tu User
                .build();
    }

    public List<FacturaResponseDTO> obtenerTodas() {
        return facturaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public FacturaResponseDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        return convertirAResponseDTO(factura);
    }

    public FacturaResponseDTO guardarFactura(FacturaResquestDto dto) {
        User usuario = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = Factura.builder()
                .folio(dto.getFolio())
                .emisor(dto.getEmisor())
                .montoTotal(dto.getMontoTotal())
                .fechaEmision(dto.getFechaEmision())
                .fechaVencimiento(dto.getFechaVencimiento()) // Agregado
                .estado(dto.getEstado())
                .usuario(usuario)
                .build();

        Factura nuevaFactura = facturaRepository.save(factura);
        return convertirAResponseDTO(nuevaFactura);
    }

    public FacturaResponseDTO actualizarFactura(Long id, FacturaResquestDto dto){
        Factura facturaExistente = facturaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Factura no encotrada"));

        User user=userRepository.findById(dto.getUsuarioId())
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        facturaExistente.setFolio(dto.getFolio());
        facturaExistente.setEmisor(dto.getEmisor());
        facturaExistente.setMontoTotal(dto.getMontoTotal());
        facturaExistente.setFechaEmision(dto.getFechaEmision());
        facturaExistente.setFechaVencimiento(dto.getFechaVencimiento()); // Agregado
        facturaExistente.setEstado(dto.getEstado());
        facturaExistente.setUsuario(user);
        Factura facturaActualizada = facturaRepository.save(facturaExistente);
        return convertirAResponseDTO(facturaActualizada);
    }

    public void elilimarFactura(Long id ){
        facturaRepository.deleteById(id);
    }

    public List<FacturaResponseDTO> obtenerPorEstadoVencimiento(EstadoFactura estado, LocalDate fechaVencimiento) {
        return facturaRepository.findByEstadoAndFechaVencimiento(estado, fechaVencimiento).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

}
