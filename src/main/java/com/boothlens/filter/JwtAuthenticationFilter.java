package com.boothlens.filter;

import com.boothlens.dto.jwt.JwtProfile;
import com.boothlens.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT Authentication Filter
 * Extracts JWT token from Authorization Header and validates it
 * Sets authentication in SecurityContext if token is valid
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 從 Header 提取 Bearer Token
            String jwt = extractTokenFromHeader(request);

            if (StringUtils.hasText(jwt)) {
                // 2. 解析 Token (這一步已經包含了驗證簽名與過期時間)
                JwtProfile profile = jwtService.getJwtProfile(jwt);

                // 3. 如果 Token 有效，且當前 SecurityContext 尚未被認證
                if (profile != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    // 4. 轉換 Role 為 Spring Security 的 GrantedAuthority
                    List<GrantedAuthority> authorities = getAuthoritiesFromRole(profile.getRole());

                    // 5. 建立 AuthenticationToken，直接把 JwtProfile 當作 Principal
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            profile, 
                            null, 
                            authorities
                    );
                    
                    // 紀錄 Request 來源資訊
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. 存入 SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        // 繼續執行 Filter Chain
        filterChain.doFilter(request, response);
    }

    // Extract JWT token from HTTP Authorization header
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 效能比 replace 好
        }
        return null;
    }

    // Map integer role to Spring Security Authorities
    private List<GrantedAuthority> getAuthoritiesFromRole(int role) {
        String roleName = switch (role) {
            case 2 -> "ROLE_CONTRACTOR";
            case 3 -> "ROLE_ADMIN";
            case 4 -> "ROLE_SUPERADMIN";
            default -> "ROLE_CLIENT";
        };
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}
