package Proceso_Administrativo.proyecto_titulo.DTO;

import lombok.Getter;

@Getter
public class MensajeResponse {
    private final String mensaje;
    private final boolean exito;

    public MensajeResponse(String mensaje, boolean exito) {
        this.mensaje = mensaje;
        this.exito = exito;
    }

}
