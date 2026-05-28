package Proceso_Administrativo.proyecto_titulo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProyectoTituloApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoTituloApplication.class, args);
	}

}
