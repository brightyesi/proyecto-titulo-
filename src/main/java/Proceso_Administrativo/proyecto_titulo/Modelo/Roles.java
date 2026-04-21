package Proceso_Administrativo.proyecto_titulo.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false,unique = true)
    private String nameRol;

    public enum NombreRol{
        ROL_ADMIN,
        ROLE_ADMINISTRADOR,
        ROLE_EJECUTIVO
    }
}
