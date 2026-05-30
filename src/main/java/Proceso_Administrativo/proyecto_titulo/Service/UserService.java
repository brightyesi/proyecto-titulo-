package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.LoginRequest;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UserResponse;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import Proceso_Administrativo.proyecto_titulo.Security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.AuthenticationException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;       // viene del bean en SecurityConfig
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // ──────────────────────────────────────────
    // REGISTRO
    // ──────────────────────────────────────────
    @Transactional
    public UserResponse regitrar(ResgisterResquest resquest) {
        if (userRepository.existsByEmail(resquest.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + resquest.getCorreo()
            );
        }

        Roles rol = rolRepository.findByNameRol(resquest.getRoles().getNameRol())
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));

        User usuario = User.builder()
                .nombre(resquest.getNombre())
                .email(resquest.getCorreo())
                .password(passwordEncoder.encode(resquest.getPassword()))
                .rol(rol)
                .activo(true)
                .build();

        usuario = userRepository.save(usuario);

        // Generar JWT para el usuario recién registrado
        String token = jwtService.generarToken(usuario);

        return toResponse(token, usuario);
    }

    // ──────────────────────────────────────────
    // LOGIN
    // ──────────────────────────────────────────}
    @Transactional
    public UserResponse login(LoginRequest request) {
        try {
            // Spring Security valida email + password automáticamente
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCorreo(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        User user = userRepository.findByEmail(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada. Contacte al administrador.");
        }

        // Generar JWT
        String token = jwtService.generarToken(user);

        return toResponse(token, user);
    }

    // ──────────────────────────────────────────
    // Helper
    // ──────────────────────────────────────────
    private UserResponse toResponse(String token, User usuario) {
        return new UserResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNameRol().name()
        );
    }
}
