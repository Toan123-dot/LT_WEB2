package com.truongquoctoan.example01.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // API JWT không dùng CSRF
                .csrf(csrf -> csrf.disable())

                // Cấu hình CORS
                .cors(cors -> cors
                        .configurationSource(
                                corsConfigurationSource()
                        )
                )

                // JWT không sử dụng HTTP Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // CORS PREFLIGHT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // =================================================
                        // SWAGGER / OPENAPI
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        )
                        .permitAll()

                        // =================================================
                        // PUBLIC AUTH ENDPOINTS
                        // =================================================

                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/ws/**"
                        )
                        .permitAll()

                        // =================================================
                        // PUBLIC PAYMENT CALLBACK
                        // Phải đặt trước /api/payments/**
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/payments/vnpay/callback"
                        )
                        .permitAll()

                        // =================================================
                        // PUBLIC GET ENDPOINTS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,

                                "/api/products",
                                "/api/products/**",

                                "/api/categories",
                                "/api/categories/**",

                                "/api/promotions",
                                "/api/promotions/**",

                                "/api/tables",
                                "/api/tables/**"
                        )
                        .permitAll()

                        // =================================================
                        // ORDER ITEMS
                        // =================================================

                        .requestMatchers(
                                "/api/order-items",
                                "/api/order-items/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF",
                                "ROLE_USER"
                        )

                        // =================================================
                        // ORDERS
                        // =================================================

                        .requestMatchers(
                                "/api/orders",
                                "/api/orders/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF",
                                "ROLE_USER"
                        )

                        // =================================================
                        // PRODUCTS — ADMIN WRITE
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products",
                                "/api/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products",
                                "/api/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/products",
                                "/api/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products",
                                "/api/products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // CATEGORIES — ADMIN WRITE
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories",
                                "/api/categories/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories",
                                "/api/categories/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/categories",
                                "/api/categories/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories",
                                "/api/categories/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // TABLES
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/tables",
                                "/api/tables/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/tables",
                                "/api/tables/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/tables",
                                "/api/tables/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/tables",
                                "/api/tables/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // BILLS
                        // =================================================

                        .requestMatchers(
                                "/api/bills",
                                "/api/bills/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF"
                        )

                        // =================================================
                        // USER STATISTICS
                        // Hai rule này phải đặt trước /api/users/**
                        // =================================================

                        // USER xem thống kê của chính mình
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/me/statistics"
                        )
                        .hasAuthority("ROLE_USER")

                        // ADMIN xem thống kê tất cả khách hàng
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/customers/statistics"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // USERS — ADMIN ONLY
                        // =================================================

                        .requestMatchers(
                                "/api/users",
                                "/api/users/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // DASHBOARD
                        // =================================================

                        .requestMatchers(
                                "/api/dashboard",
                                "/api/dashboard/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // PAYMENTS
                        // =================================================

                        .requestMatchers(
                                "/api/payments",
                                "/api/payments/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF",
                                "ROLE_USER"
                        )

                        // =================================================
                        // PROMOTIONS — ADMIN WRITE
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/promotions",
                                "/api/promotions/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/promotions",
                                "/api/promotions/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/promotions",
                                "/api/promotions/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/promotions",
                                "/api/promotions/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // PROMOTION PRODUCTS — ADMIN
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/promotion-products",
                                "/api/promotion-products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/promotion-products",
                                "/api/promotion-products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/promotion-products",
                                "/api/promotion-products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/promotion-products",
                                "/api/promotion-products/**"
                        )
                        .hasAuthority("ROLE_ADMIN")

                        // =================================================
                        // SETTINGS
                        // =================================================

                        .requestMatchers(
                                "/api/settings",
                                "/api/settings/**"
                        )
                        .hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_EMPLOYEE",
                                "ROLE_STAFF"
                        )

                        // =================================================
                        // MỌI REQUEST CÒN LẠI PHẢI ĐĂNG NHẬP
                        // =================================================

                        .anyRequest()
                        .authenticated()
                )

                // Kiểm tra JWT trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Content-Type",
                        "Authorization",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}