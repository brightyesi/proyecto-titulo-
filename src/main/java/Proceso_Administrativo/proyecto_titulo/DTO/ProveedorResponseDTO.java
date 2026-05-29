package Proceso_Administrativo.proyecto_titulo.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProveedorResponseDTO {

    private Long id;
    private String rutProveedor;
    private String nombreComercial;
    private String email;
    private String telefono;
    private boolean activo;
}
