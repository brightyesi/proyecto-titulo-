package Proceso_Administrativo.proyecto_titulo.Config;

import Proceso_Administrativo.proyecto_titulo.Modelo.AlertConfig;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.AlertaConfigRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;
    private final AlertaConfigRepository alertaConfigRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, AlertaConfigRepository alertaConfigRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.alertaConfigRepository = alertaConfigRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (rolRepository.count() == 0) {
            rolRepository.save(new Roles(null, Roles.NombreRol.ROLE_ADMIN));
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

        if (userRepository.count() == 0) {
            Roles rolAdmin = rolRepository.findByNameRol(Roles.NombreRol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            User admin = User.builder()
                    .nombre("Admin Sistema")
                    .email("admin.sistema@empresa.cl")
                    .password(passwordEncoder.encode("12345678"))
                    .rol(rolAdmin)
                    .activo(true)
                    .build();

            userRepository.save(admin);
            log.info("Usuario ADMIN inicial creado: admin.sistema@empresa.cl / 12345678");
        }
    }
}
