package com.truongquoctoan.example01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // ========== PUBLIC ENDPOINTS ==========
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/ws/**",
                                "/api/payments/vnpay/callback")
                        .permitAll()

                        // ========== PUBLIC GET ONLY ==========
                        .requestMatchers(HttpMethod.GET,
                                "/api/products/**",
                                "/api/categories/**",
                                "/api/promotions/**",
                                "/api/tables/**")
                        .permitAll()

                        // ========== ⚠️ CRITICAL: ORDER ITEMS MUST BE FIRST ==========
                        .requestMatchers("/api/order-items/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF", "ROLE_USER")

                        // ========== ORDERS (comes AFTER order-items) ==========
                        .requestMatchers("/api/orders/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF", "ROLE_USER")

                        // ========== PRODUCTS (WRITE operations - ADMIN only) ==========
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")

                        // ========== CATEGORIES (WRITE operations - ADMIN only) ==========
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority("ROLE_ADMIN")

                        // ========== TABLES ==========
                        .requestMatchers(HttpMethod.POST, "/api/tables/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tables/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/tables/**").hasAuthority("ROLE_ADMIN")

                        // ========== BILLS ==========
                        .requestMatchers("/api/bills/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF")

                        // ========== USERS ==========
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                        // ========== DASHBOARD ==========
                        .requestMatchers("/api/dashboard/**").hasAuthority("ROLE_ADMIN")

                        // ========== PAYMENTS ==========
                        .requestMatchers("/api/payments/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF", "ROLE_USER")

                        // ========== PROMOTIONS (WRITE - ADMIN only) ==========
                        .requestMatchers(HttpMethod.POST, "/api/promotions/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/promotions/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotions/**").hasAuthority("ROLE_ADMIN")

                        // ========== PROMOTION PRODUCTS (WRITE - ADMIN only) ==========
                        .requestMatchers(HttpMethod.POST, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")

                        // ========== SETTINGS ==========
                        .requestMatchers("/api/settings/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_STAFF")

                        // ========== ALL OTHER REQUESTS ==========
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(java.util.Arrays.asList(
                "Content-Type",
                "Authorization",
                "Accept",
                "Origin",
                "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}