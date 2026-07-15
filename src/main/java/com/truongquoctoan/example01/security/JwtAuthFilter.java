package com.truongquoctoan.example01.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Không có Bearer token thì để Spring Security xử lý tiếp
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            String username = jwtUtils.getUsernameFromJwt(token);
            String rawRole = jwtUtils.getRoleFromJwt(token);

            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("JWT không chứa username");
            }

            if (rawRole == null || rawRole.isBlank()) {
                throw new IllegalArgumentException("JWT không chứa role");
            }

            /*
             * Chuẩn hóa:
             * admin       -> ADMIN
             * ROLE_ADMIN  -> ADMIN
             * role_admin  -> ADMIN
             */
            String normalizedRole = rawRole
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (normalizedRole.startsWith("ROLE_")) {
                normalizedRole = normalizedRole.substring(5);
            }

            List<GrantedAuthority> authorities = new ArrayList<>();

            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + normalizedRole)
            );

            // STAFF và EMPLOYEE được xem là tương đương
            if ("STAFF".equals(normalizedRole)) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_EMPLOYEE")
                );
            } else if ("EMPLOYEE".equals(normalizedRole)) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_STAFF")
                );
            }

            // Chỉ đặt Authentication khi chưa có người dùng được xác thực
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContext context =
                        SecurityContextHolder.createEmptyContext();

                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }

            System.out.println("=================================");
            System.out.println("JWT request     : " + request.getMethod()
                    + " " + request.getRequestURI());
            System.out.println("JWT username    : " + username);
            System.out.println("JWT raw role    : " + rawRole);
            System.out.println("JWT normalized  : " + normalizedRole);
            System.out.println("JWT authorities : " + authorities);
            System.out.println("=================================");

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            SecurityContextHolder.clearContext();

            System.err.println("=================================");
            System.err.println("JWT authentication failed");
            System.err.println("Request : " + request.getMethod()
                    + " " + request.getRequestURI());
            System.err.println("Error   : "
                    + exception.getClass().getSimpleName());
            System.err.println("Message : " + exception.getMessage());
            System.err.println("=================================");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().write("""
                    {
                      "status": 401,
                      "message": "Token không hợp lệ hoặc đã hết hạn"
                    }
                    """);
        }
    }
}