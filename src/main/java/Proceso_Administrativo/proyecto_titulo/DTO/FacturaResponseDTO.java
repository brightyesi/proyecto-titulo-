package Proceso_Administrativo.proyecto_titulo.DTO;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
public class FacturaResponseDTO {
    private Long id;
    private String folio;
    private String emisor;
    private BigDecimal montoTotal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private EstadoFactura estado;
    private Long usuarioId;
    private String usuarioNombre;
}
