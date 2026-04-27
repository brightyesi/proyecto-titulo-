package Proceso_Administrativo.proyecto_titulo.DTO;

import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class ResgisterResquest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 25, message = "El nombre no debe superar los 25 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 50, message = "El correo no debe superar los 50 caracteres")
    @Email(message = "Formato de correo invalido")
    private String correo;

    @NotBlank(message = "El Contraseña es obligatorio")
    @Size(max = 6, message = "El contraseña no debe superar los 6 caracteres")
    private String password;

    @NotBlank(message = "rol es obligatorio")
    private Roles roles;

}
