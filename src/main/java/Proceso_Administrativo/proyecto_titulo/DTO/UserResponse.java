package Proceso_Administrativo.proyecto_titulo.DTO;


import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String nombre;
    private final String email;
    private final String rol;

    public UserResponse(Long id,String nombre, String email, String rol){
        this.id =id;
        this.nombre=nombre;
        this.email=email;
        this.rol=rol;
    }


}
