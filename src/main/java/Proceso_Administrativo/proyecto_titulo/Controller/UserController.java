package Proceso_Administrativo.proyecto_titulo.Controller;


import Proceso_Administrativo.proyecto_titulo.DTO.LoginRequest;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UserResponse;
import Proceso_Administrativo.proyecto_titulo.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController (UserService userService){

        this.userService = userService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UserResponse> registrar(@Valid @RequestBody ResgisterResquest request) {
        UserResponse response = userService.regitrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
