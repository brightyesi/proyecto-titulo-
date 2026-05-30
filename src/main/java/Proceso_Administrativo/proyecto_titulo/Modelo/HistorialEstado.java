package Proceso_Administrativo.proyecto_titulo.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "historial_estado")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne
    @JoinColumn(name = "idFactura ", nullable = false)
    private Factura factura;

    @Enumerated
    @Column(nullable = false)
    private EstadoFactura estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoFactura estadoAterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo")
    private EstadoFactura estadoNuevo;


    @Column(name = "fecha_cambio",nullable = false)
    private LocalDateTime fechaCambio;




}
