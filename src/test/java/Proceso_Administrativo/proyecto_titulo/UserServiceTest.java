package Proceso_Administrativo.proyecto_titulo;

import Proceso_Administrativo.proyecto_titulo.DTO.LoginRequest;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UserResponse;
import Proceso_Administrativo.proyecto_titulo.DTO.UsuarioResponse;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import Proceso_Administrativo.proyecto_titulo.Security.JwtService;
import Proceso_Administrativo.proyecto_titulo.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User usuario;
    private Roles rolEjecutivo;

    @BeforeEach
    void setUp() {
        rolEjecutivo = new Roles();
        rolEjecutivo.setNameRol(Roles.NombreRol.ROLE_EJECUTIVO);

        usuario = User.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juanperez@gmail.com")
                .password("hashed")
                .rol(rolEjecutivo)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Login exitoso devuelve token")
    void login_exito() {
        LoginRequest req = new LoginRequest();
        req.setCorreo("juanperez@gmail.com");
        req.setPassword("12345678");

        when(userRepository.findByEmail("juanperez@gmail.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("token-falso");

        UserResponse response = userService.login(req);

        assertNotNull(response);
        assertEquals("token-falso", response.getToken());
    }

    @Test
    @DisplayName("Login con cuenta desactivada lanza excepcion")
    void login_cuentaInactiva() {
        usuario.setActivo(false);
        LoginRequest req = new LoginRequest();
        req.setCorreo("juanperez@gmail.com");
        req.setPassword("12345678");

        when(userRepository.findByEmail("juanperez@gmail.com")).thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class, () -> userService.login(req));
    }

    @Test
    @DisplayName("Crear usuario con email existente lanza excepcion")
    void crearUsuario_emailDuplicado() {
        ResgisterResquest req = new ResgisterResquest();
        req.setCorreo("juanperez@gmail.com");

        when(userRepository.existsByEmail("juanperez@gmail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.crearUsuario(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear usuario exitoso devuelve UsuarioResponse")
    void crearUsuario_exito() {
        ResgisterResquest req = new ResgisterResquest();
        req.setNombre("Maria");
        req.setCorreo("maria@empresa.cl");
        req.setPassword("12345678");
        req.setRoles(rolEjecutivo);

        when(userRepository.existsByEmail("maria@empresa.cl")).thenReturn(false);
        when(rolRepository.findByNameRol(Roles.NombreRol.ROLE_EJECUTIVO)).thenReturn(Optional.of(rolEjecutivo));
        when(passwordEncoder.encode("12345678")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = userService.crearUsuario(req);

        assertNotNull(response);
        assertEquals("maria@empresa.cl", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Eliminar usuario lo desactiva")
    void eliminarUsuario_desactiva() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        userService.eliminarUsuario(1L);

        assertFalse(usuario.isActivo());
        verify(userRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Eliminar usuario inexistente lanza excepcion")
    void eliminarUsuario_noEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.eliminarUsuario(99L));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Activar usuario lo deja activo")
    void activarUsuario_exito() {
        usuario.setActivo(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        userService.activarUsuario(1L);

        assertTrue(usuario.isActivo());
        verify(userRepository, times(1)).save(usuario); // se guardó
    }

    @Test
    @DisplayName("Activar usuario inexistente lanza excepcion")
    void activarUsuario_noEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.activarUsuario(99L));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar definitivo borra el usuario")
    void eliminarDefinitivo_exito() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        userService.eliminarDefinitivo(1L);

        verify(userRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Eliminar definitivo de usuario inexistente lanza excepcion")
    void eliminarDefinitivo_noEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.eliminarDefinitivo(99L));
        verify(userRepository, never()).delete(any());
    }
}