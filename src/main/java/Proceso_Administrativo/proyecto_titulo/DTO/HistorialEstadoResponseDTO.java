package Proceso_Administrativo.proyecto_titulo.DTO;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class HistorialEstadoResponseDTO {

    private Long id;
    private Long facturaId;
    private String folioFactura;
    private EstadoFactura estadoAnterior;
    private EstadoFactura estadoNuevo;
    private LocalDateTime fechaCambio;

}
