package Proceso_Administrativo.proyecto_titulo.Service;

import Proceso_Administrativo.proyecto_titulo.DTO.LoginRequest;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UserResponse;
import Proceso_Administrativo.proyecto_titulo.Modelo.Roles;
import Proceso_Administrativo.proyecto_titulo.Modelo.User;
import Proceso_Administrativo.proyecto_titulo.Repository.RolRepository;
import Proceso_Administrativo.proyecto_titulo.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private RolRepository rolRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserResponse regitrar(ResgisterResquest resquest){
        if (userRepository.existsByCorreo(resquest.getCorreo())){
            throw new IllegalArgumentException(
                    "ya exixte un usuario con el email:"+ resquest.getCorreo()
            );
        }
        Roles rol = rolRepository.finByName(resquest.getRoles().getNameRol())
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));

        User usuario = User.builder()
                .nombre(resquest.getNombre())
                .email(resquest.getCorreo())
                .password(resquest.getPassword())
                .password(bCryptPasswordEncoder.encode(resquest.getPassword()))
                .rol(rol)
                .activo(true)
                .build();
        usuario =userRepository.save(usuario);
        return toResponse(usuario);
    }

    public UserResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getCorreo())
                .orElseThrow(()
                        -> new RuntimeException("Email o contraseña incorrecto"));

        if (!user.isActivo()){
            throw new RuntimeException("La cuenta esta desactivada");
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }

        return toResponse(user);
    }


    private UserResponse toResponse(User usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNameRol().name()
        );
    }

}
