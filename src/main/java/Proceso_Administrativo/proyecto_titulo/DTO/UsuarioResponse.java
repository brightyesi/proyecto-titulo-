package Proceso_Administrativo.proyecto_titulo.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private  Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
}
