package com.petfarewell.auth.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtTokenProvider.parseClaims(token);
                String subject = claims.getSubject();

                log.info("subject: {}", subject);
                log.info("claims: {}", claims);

                if (subject != null && !subject.isBlank()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    subject,
                                    null,
                                    java.util.Collections.emptyList()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT authentication successful for user: {}", subject);
                } else {
                    log.warn("JWT token has invalid subject: {}", subject);
                    SecurityContextHolder.clearContext();
                }

            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (MalformedJwtException e) {
                log.warn("JWT token malformed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (UnsupportedJwtException e) {
                log.warn("JWT token unsupported: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (IllegalArgumentException e) {
                log.warn("JWT token has invalid argument: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("JWT token validation failed: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}

