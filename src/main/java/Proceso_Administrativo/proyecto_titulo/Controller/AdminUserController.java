package Proceso_Administrativo.proyecto_titulo.Controller;

import Proceso_Administrativo.proyecto_titulo.DTO.MensajeResponse;
import Proceso_Administrativo.proyecto_titulo.DTO.ResgisterResquest;
import Proceso_Administrativo.proyecto_titulo.DTO.UsuarioResponse;
import Proceso_Administrativo.proyecto_titulo.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    //crear usuario
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody ResgisterResquest resquest) {
        return new ResponseEntity<>(userService.crearUsuario(resquest), HttpStatus.CREATED);
    }

    // listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(userService.listarUsuarios());
    }

    // eliminar/desactivar usuarios
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.ok(new MensajeResponse("Usuario desactivado correctamente", true));
    }
}
