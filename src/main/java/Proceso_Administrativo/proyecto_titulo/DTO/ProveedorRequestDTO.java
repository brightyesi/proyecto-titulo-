package Proceso_Administrativo.proyecto_titulo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProveedorRequestDTO {

    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 12, message = "El RUT no puede superar 12 caracteres")
    private String rutProveedor;

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreComercial;

    @Email(message = "El email no tiene formato válido")
    private String email;

    private String telefono;
}
