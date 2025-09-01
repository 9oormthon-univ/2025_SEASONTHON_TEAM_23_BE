package com.petfarewell.auth.security;

import java.io.IOException;

import com.petfarewell.auth.entity.User;
import com.petfarewell.auth.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtTokenProvider.parseClaims(token);
                String userId = claims.getSubject();

                if (userId != null && !userId.isBlank()) {
                    // 1. userId로 DB에서 User 엔티티 조회
                    User user = userRepository.findById(Long.parseLong(userId))
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    // 2. User 엔티티로 CustomUserDetails 객체 생성
                    UserDetails userDetails = new CustomUserDetails(user);

                    // 3. CustomUserDetails 객체를 Principal로 사용하여 Authentication 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, // Principal이 CustomUserDetails 객체
                                    null,
                                    userDetails.getAuthorities() // 권한 정보도 UserDetails에서 가져옴
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
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

