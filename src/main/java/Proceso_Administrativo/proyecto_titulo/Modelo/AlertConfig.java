package Proceso_Administrativo.proyecto_titulo.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alert_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AlertConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dias_previos", nullable = false)
    private int diasPrevios;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
