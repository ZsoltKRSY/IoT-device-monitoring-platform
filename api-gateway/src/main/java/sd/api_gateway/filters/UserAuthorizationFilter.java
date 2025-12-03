package sd.api_gateway.filters;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import sd.api_gateway.util.JwtUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserAuthorizationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(UserAuthorizationFilter.class);

    private final JwtUtil jwtUtil;

    private static final Pattern DEVICES_USER_PATTERN = Pattern.compile("/devices/user/(\\d+)");
    private static final Pattern USERS_PATTERN = Pattern.compile("/users/(\\d+)");

    public UserAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        Matcher devicesMatcher = DEVICES_USER_PATTERN.matcher(path);
        Matcher usersMatcher = USERS_PATTERN.matcher(path);

        if (devicesMatcher.matches() || usersMatcher.matches()) {
            String pathUserId = devicesMatcher.matches() ?
                    devicesMatcher.group(1) : usersMatcher.group(1);

            return ReactiveSecurityContextHolder.getContext()
                    .flatMap(securityContext -> {
                        Authentication authentication = securityContext.getAuthentication();

                        if (authentication == null || !authentication.isAuthenticated()) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        String token = authentication.getCredentials().toString();
                        String tokenUserId = extractUserId(token);

                        if (tokenUserId == null) {
                            log.warn("Could not extract user ID from token");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                        if (!isAdmin && !tokenUserId.equals(pathUserId)) {
                            log.warn("User {} attempted to access resources of user {}",
                                    tokenUserId, pathUserId);
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }

                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("No security context found for protected path: {}", path);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }));
        }

        return chain.filter(exchange);
    }

    private String extractUserId(String token) {
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            return claims.getSubject(); // 'sub' claim contains userId
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }
}