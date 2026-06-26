package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.LoginRequest;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UserResponse;
import Proceso_Administrativo.proyecto_titulo.DTO.UsuarioResponse;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import Proceso_Administrativo.proyecto_titulo.Security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.stream.Collectors;

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


    // REGISTRO
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

    // LOGIN
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

    // Helper
    private UserResponse toResponse(String token, User usuario) {
        return new UserResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNameRol().name()
        );
    }

    //solo ADmIN
    @Transactional
    public UsuarioResponse crearUsuario(ResgisterResquest resquest) {
        if (userRepository.existsByEmail(resquest.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + resquest.getCorreo());
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
        return toUsuarioResponse(usuario);
    }

    //listar los usuarios
    public List<UsuarioResponse> listarUsuarios() {
        return userRepository.findAll().stream()
                .map(this::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    // elimina/desactiva usuarios

    @Transactional
    public void eliminarUsuario(Long id) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setActivo(false);
        userRepository.save(usuario);
    }

    //helper

    private UsuarioResponse toUsuarioResponse(User usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNameRol().name())
                .activo(usuario.isActivo())
                .build();
    }

    @Transactional
    public void activarUsuario(Long id) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setActivo(true);
        userRepository.save(usuario);
    }

    @Transactional
    public void eliminarDefinitivo(Long id) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        try {
            userRepository.delete(usuario);
            userRepository.flush(); //fuerza el borrado
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                    "No se puede eliminar: el usuario tiene facturas asociadas. Solo se puede desactivar.");
        }
    }
}
