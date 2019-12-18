package app.jwt;

import app.repository.UserRepository;
import enums.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {

    @Value("${JWT_SECRET_KEY:verysecretkey}")
    private String secretKey;

    @Value("${JWT_VALIDITY_MS:86400000}")
    private int validityInMilliseconds;

    private final UserRepository userRepository;

    @Autowired
    public TokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UUID username, Role role) {
        Claims claims = Jwts.claims().setSubject(username.toString());
        claims.put("role", role.toString());

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private UUID getID(String token) {
        return UUID.fromString(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }

    Authentication getAuthentication(String token) {
        UserDetails user = this.userRepository.findById(getID(token))
                .orElseThrow(() -> new InvalidJwtAuthenticationException("User for this token does not exist"));

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else return null;
    }

    boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }
}