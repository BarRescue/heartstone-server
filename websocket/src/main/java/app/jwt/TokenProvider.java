package app.jwt;

import app.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {
    @Value("${JWT_SECRET_KEY:verysecretkey}")
    private String secretKey;

    @Value("${JWT_VALIDITY_MS:86400000}")
    private int validityInMilliseconds;

    @PostConstruct
    public void initSecret() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private UUID getID(String token) {
        return UUID.fromString(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }

    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("username", String.class);
    }

    public Authentication getAuthentication(String token) {
        User user = new User();
        user.setId(getID(token));
        user.setUserName(getUsername(token));

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String resolveToken(String bearer) {
        if(bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        } else {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JWT");
        }
    }
}
