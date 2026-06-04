package com.tl.tutor_link.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security setup. Configures CORS for the frontend dev server,
 * disables CSRF (stateless JWT auth doesn't need it), defines per-endpoint
 * authorization rules, and registers the JWT filter to run before the
 * standard authentication filter.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;


    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/courses/**").permitAll()

                        .requestMatchers("/reviews/tutor/**").permitAll()
                        .requestMatchers("/reviews/booking/**").permitAll()
                        .requestMatchers("/tutors", "/tutors/*", "/tutors/search").permitAll()

                        .requestMatchers("/health").permitAll()

                        // Tutor-only endpoints (managing their own profile)
                        .requestMatchers("/tutors/me/**").hasRole("TUTOR")

                        // Authenticated user endpoints
                        .requestMatchers("/users/me", "/users/me/become-tutor")
                        .hasAnyRole("STUDENT", "TUTOR", "ADMIN")

                        .requestMatchers("/upload/**")
                        .hasAnyRole("STUDENT", "TUTOR", "ADMIN")

                        .requestMatchers("/bookings/**")
                        .hasAnyRole("STUDENT", "TUTOR", "ADMIN")

                        .requestMatchers("/reviews/**")
                        .hasAnyRole("STUDENT", "TUTOR", "ADMIN")
                        // Admin-only
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
