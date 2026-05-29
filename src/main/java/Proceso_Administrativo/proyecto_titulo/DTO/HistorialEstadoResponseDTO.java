package Proceso_Administrativo.proyecto_titulo.DTO;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class HistorialEstadoResponseDTO {

    private Long id;
    private Long facturaId;
    private String folioFactura;
    private EstadoFactura estado;
    private LocalDateTime fechaCambio;

}
