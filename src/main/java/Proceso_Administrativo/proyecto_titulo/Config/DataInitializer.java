package Proceso_Administrativo.proyecto_titulo.Config;

import Proceso_Administrativo.proyecto_titulo.Modelo.AlertConfig;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaConfigRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;
    private final AlertaConfigRepository alertaConfigRepository;

    public DataInitializer(RolRepository rolRepository, AlertaConfigRepository alertaConfigRepository) {
        this.rolRepository = rolRepository;
        this.alertaConfigRepository = alertaConfigRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (rolRepository.count() == 0) {
            rolRepository.save(new Roles(null, Roles.NombreRol.ROLE_ADMINISTRADOR));
            rolRepository.save(new Roles(null, Roles.NombreRol.ROLE_EJECUTIVO));
            log.info("Roles iniciados insertados.");
        }

        if (alertaConfigRepository.count() == 0) {
            alertaConfigRepository.save(new AlertConfig(null, 5, true));
            alertaConfigRepository.save(new AlertConfig(null, 3, true));
            alertaConfigRepository.save(new AlertConfig(null, 0, true));
            log.info("Configuración de alertas terminada: 5, 3 y 0 días.");
        }
    }
}
