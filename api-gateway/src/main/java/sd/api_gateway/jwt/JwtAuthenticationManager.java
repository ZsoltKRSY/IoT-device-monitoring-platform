package sd.api_gateway.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sd.api_gateway.util.JwtUtil;

import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationManager.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid token provided");
            return Mono.empty();
        }

        String role = jwtUtil.extractRole(token);
        if (role == null) {
            log.warn("No role found in token");
            return Mono.empty();
        }

        log.debug("Token validated successfully for role: {}", role);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                token,
                token,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );

        return Mono.just(auth);
    }
}