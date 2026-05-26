package Proceso_Administrativo.proyecto_titulo.Modelo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne (fetch =FetchType.EAGER )
    @JoinColumn(name = "rol_id",nullable = false)
    private Roles rol;

    @Column(nullable = false)
    private boolean activo;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // nameRol ya tiene el prefijo: ROL_ADMIN, ROLE_ADMINISTRADOR, ROLE_EJECUTIVO
        return List.of(new SimpleGrantedAuthority(rol.getNameRol().name()));
    }

    @Override
    public String getUsername() {
        return email; // Spring Security usará el email como identificador
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return activo; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return activo; }

}