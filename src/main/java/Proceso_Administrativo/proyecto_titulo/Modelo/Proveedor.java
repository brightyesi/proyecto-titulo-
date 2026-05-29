package Proceso_Administrativo.proyecto_titulo.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut-´proveedor", nullable = false, unique = true, length = 12)
    private String rutProveedor;

    @Column(name = "nombre_comercial", nullable = false, length = 100)
    private String nombreComercial;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

}
