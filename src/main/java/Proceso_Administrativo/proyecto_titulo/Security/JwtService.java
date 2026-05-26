package Proceso_Administrativo.proyecto_titulo.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;           // Claims
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;             // Jwts.builder() y Jwts.parser()
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;      // Decoders.BASE64
import io.jsonwebtoken.security.Keys;    // Keys.hmacShaKeyFor()
import java.util.function.Function;      // Function<Claims, T>
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Component
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Generar token
    public String generarToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails);
    }

    public String generarToken(Map<String, Object> claimsExtra, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claimsExtra)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();

    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraerTodosClaims(token));
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            String email = extraerEmail(token);
            return email.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (MalformedJwtException e) {
            logger.error("Token malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Token vacío: {}", e.getMessage());
        }
        return false;
    }

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSigningKey() {
        // Genera clave segura a partir del secret en application.properties
        byte[] keyBytes = java.util.Base64.getEncoder().encode(secretKey.getBytes());
        return Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(
                new String(keyBytes)
        ));
    }

}
