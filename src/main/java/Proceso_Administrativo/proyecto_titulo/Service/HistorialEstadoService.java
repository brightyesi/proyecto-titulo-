package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.HistorialEstadoResponseDTO;
import Proceso_Administrativo.proyecto_titulo.Modelo.HistorialEstado;
import Proceso_Administrativo.proyecto_titulo.Repository.HistorialEstadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoService(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    public List<HistorialEstadoResponseDTO> obtenerHistorialPorFactura(Long facturaId){
        return historialEstadoRepository
                .findByFactura_IdOrderByFechaCambioDesc(facturaId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private HistorialEstadoResponseDTO convertirADTO(HistorialEstado historial){
        return HistorialEstadoResponseDTO.builder()
                .id(historial.getIdHistorial())
                .facturaId(historial.getFactura().getId())
                .folioFactura(historial.getFactura().getFolio())
                .estadoAnterior(historial.getEstadoAnterior())
                .estadoNuevo(historial.getEstadoNuevo())
                .fechaCambio(historial.getFechaCambio())
                .build();
    }
}
