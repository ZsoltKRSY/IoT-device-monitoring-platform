package sd.authentication.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "heardtheyreformingthedawnguardvampirehuntersorsomething";
    private static final long EXPIRATION_TIME = 864_000_000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String username, boolean isAdmin) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", isAdmin ? "ADMIN" : "USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }
}
