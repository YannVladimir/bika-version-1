package com.bika.security.filter;

import com.bika.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.debug("JwtAuthenticationFilter: Processing request to: {}", request.getRequestURI());
        log.debug("JwtAuthenticationFilter: Authorization header: {}", authHeader != null ? "Present" : "Absent");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("JwtAuthenticationFilter: No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.debug("JwtAuthenticationFilter: Extracted JWT token (length: {})", jwt.length());
        
        try {
            username = jwtService.extractUsername(jwt);
            log.debug("JwtAuthenticationFilter: Extracted username from token: '{}'", username);
        } catch (Exception e) {
            log.error("JwtAuthenticationFilter: Error extracting username from token", e);
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("JwtAuthenticationFilter: Username found and no authentication context exists, validating token");
            
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("JwtAuthenticationFilter: User found: {}", userDetails.getUsername());
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("JwtAuthenticationFilter: Token is valid, setting authentication context");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JwtAuthenticationFilter: Authentication context set successfully");
                } else {
                    log.warn("JwtAuthenticationFilter: Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                log.error("JwtAuthenticationFilter: Error validating token for user: {}", username, e);
            }
        } else {
            log.debug("JwtAuthenticationFilter: Username is null or authentication context already exists");
        }
        
        filterChain.doFilter(request, response);
    }
} 