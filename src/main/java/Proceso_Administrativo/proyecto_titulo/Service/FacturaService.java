package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResponseDTO;
import Proceso_Administrativo.proyecto_titulo.DTO.FacturaResquestDto;
import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import Proceso_Administrativo.proyecto_titulo.Modelo.Factura;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.FacturaRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.HistorialEstadoRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UserRepository userRepository;
    private final HistorialEstadoRepository historialEstadoRepository;

    @Autowired
    public FacturaService (FacturaRepository facturaRepository, UserRepository userRepository, HistorialEstadoRepository historialEstadoRepository){
        this.facturaRepository=facturaRepository;
        this.userRepository=userRepository;
        this.historialEstadoRepository = historialEstadoRepository;
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
        return facturaRepository.findByEliminadoFalse().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // 2. Modificado: Solo obtiene si no está eliminada
    public FacturaResponseDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada o está en la papelera"));
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
        EstadoFactura estadoAnterior =facturaExistente.getEstado();
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

    public void eliminarFactura(Long id){
        Factura factura= facturaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(()-> new RuntimeException("Factura no encontrada"));

        if(!factura.isEliminado()){
            throw new RuntimeException("La factura no esta en la papelera");
        }
    }

    public List<FacturaResponseDTO> obtenerPorEstadoVencimiento(EstadoFactura estado, LocalDate fechaVencimiento) {
        return facturaRepository.findByEstadoAndFechaVencimientoAndEliminadoFalse(estado, fechaVencimiento).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public FacturaResponseDTO restaurarFactura(Long id){
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Factura no encotrada"));
        if (!factura.isEliminado()){
            throw new RuntimeException("La factura no esta en la papelera");
        }
        factura.setEliminado(false);
        factura.setFechaEliminacion(LocalDate.now());
        Factura facturaRestaurada = facturaRepository.save(factura);
        return convertirAResponseDTO(facturaRestaurada);
    }

    public List<FacturaResponseDTO>obtenerEliminadas(){
        return facturaRepository.findByEliminadoTrue()
                .stream().map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

}
