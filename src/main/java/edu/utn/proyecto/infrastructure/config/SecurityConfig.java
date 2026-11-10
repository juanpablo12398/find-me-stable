package edu.utn.proyecto.infrastructure.config;

import edu.utn.proyecto.security.JwtCookieFilter;
import edu.utn.proyecto.security.JwtVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${security.hsts.enabled:false}")
    private boolean hstsEnabled;

    public SecurityConfig() {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtVerifier jwtVerifier) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> {
                    headers.frameOptions(frame -> frame.deny());
                    headers.contentTypeOptions(Customizer.withDefaults());
                    headers.referrerPolicy(rp ->
                            rp.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
                    headers.permissionsPolicy(pp ->
                            pp.policy("geolocation=(self), camera=(), microphone=()"));
                    headers.contentSecurityPolicy(csp ->
                            csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https://*.tile.openstreetmap.org"));
                    headers.addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "1; mode=block"));
                    if (hstsEnabled) {
                        headers.httpStrictTransportSecurity(hsts ->
                                hsts.includeSubDomains(true).maxAgeInSeconds(31536000));
                    } else {
                        headers.httpStrictTransportSecurity(hsts -> hsts.disable());
                    }
                })
                .addFilterBefore(new JwtCookieFilter(jwtVerifier), AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/css/**", "/*.css").permitAll()
                        .requestMatchers("/js/**", "/*.js").permitAll()
                        .requestMatchers("/js/config/**", "/js/services/**", "/js/ui/**", "/js/utils/**", "/js/features/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**", "/assets/**", "/static/**", "/favicon.ico").permitAll()

                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()

                        .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/avistadores", "/api/v1/avistadores").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/desaparecidos", "/api/v1/desaparecidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/desaparecidos/estadisticas", "/api/v1/desaparecidos/estadisticas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/avistamientos/**", "/api/v1/avistamientos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/avistamientos/poligono", "/api/v1/avistamientos/poligono").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/avistamientos/buscar/**", "/api/v1/avistamientos/buscar/**").permitAll()
                        // Swagger UI y OpenAPI docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers("/ws/**").permitAll() // WebSockets

                        .requestMatchers(HttpMethod.GET, "/api/files/**", "/api/v1/files/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/files/**", "/api/v1/files/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/files/**", "/api/v1/files/**").hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/desaparecidos", "/api/v1/desaparecidos").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/avistamientos/**", "/api/v1/avistamientos/**").authenticated()

                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin",
                "X-Requested-With", "Cache-Control"
        ));
        configuration.setExposedHeaders(Arrays.asList("Location", "Content-Disposition"));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}