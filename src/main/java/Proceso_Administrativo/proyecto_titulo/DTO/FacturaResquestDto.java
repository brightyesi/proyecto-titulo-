package Proceso_Administrativo.proyecto_titulo.DTO;

import Proceso_Administrativo.proyecto_titulo.Modelo.EstadoFactura;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Getter
@Setter
public class FacturaResquestDto {
    @NotBlank(message = "El folio es obligatorio")
    private String folio;

    @NotBlank(message = "El emisor es obligatorio")
    private String emisor;

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal montoTotal;

    @NotNull(message = "La fecha de emision es obligatoria")
    private LocalDate fechaEmision;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El estado es obligatorio")
    private EstadoFactura estado;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

}
