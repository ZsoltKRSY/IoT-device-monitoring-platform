package sd.api_gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import sd.api_gateway.entrypoint.CustomAuthenticationEntryPoint;
import sd.api_gateway.filters.UserAuthorizationFilter;
import sd.api_gateway.handlers.CustomAccessDeniedHandler;
import sd.api_gateway.jwt.JwtAuthenticationManager;
import sd.api_gateway.jwt.JwtSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationManager authenticationManager;
    private final JwtSecurityContextRepository securityContextRepository;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final UserAuthorizationFilter userAuthorizationFilter;

    public SecurityConfig(JwtAuthenticationManager authenticationManager,
                          JwtSecurityContextRepository securityContextRepository,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          UserAuthorizationFilter userAuthorizationFilter) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userAuthorizationFilter = userAuthorizationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .addFilterAt(userAuthorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        .pathMatchers(HttpMethod.GET, "/api/devices/user/*").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/users/*").hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/monitoring/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers("/api/devices/**").hasRole("ADMIN")
                        .pathMatchers("/api/users/**").hasRole("ADMIN")
                        .pathMatchers("/api/auth/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}