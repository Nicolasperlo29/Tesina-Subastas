//package org.example.usuarios.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import jakarta.annotation.Nullable;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Collections;
//
//@Nullable
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final String SECRET_KEY = "secret"; // Aquí debes poner tu clave secreta
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = getJwtFromRequest(request);
//
//        if (token != null && validateToken(token)) {
//            String email = getEmailFromToken(token);
//
//            UserDetails userDetails = new User(email, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String header = request.getHeader("Authorization");
//        if (header != null && header.startsWith("Bearer ")) {
//            return header.substring(7);
//        }
//        return null;
//    }
//
//    private boolean validateToken(String token) {
//        try {
//            byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8); // Convertir la clave secreta en bytes
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKeyBytes) // Usa el arreglo de bytes para validar el token
//                    .build()
//                    .parseClaimsJws(token); // Validar el JWT
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private String getEmailFromToken(String token) {
//        byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8); // Convertir la clave secreta en bytes
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secretKeyBytes) // Usar el arreglo de bytes para validar el token
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();  // El "subject" del JWT es el correo electrónico
//    }
//}


// LA NUEVA:

package org.example.usuarios.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        System.out.println("Request URI: " + uri);
        // Lista de rutas públicas
        if (
                uri.startsWith("/swagger-ui") ||
                        uri.startsWith("/v3/api-docs") ||
                        uri.startsWith("/auth/login") ||
                        uri.startsWith("/auth/verify") ||
                        uri.startsWith("/auth/register") ||
                        uri.startsWith("/auth/pong") ||
                        uri.startsWith("/auth/recover") ||
                        uri.startsWith("/auth/reset-password") ||
                        uri.contains("/auth/pong") ||
                        uri.contains("/user/darDeBaja") ||
                        uri.contains("/auth/verify") ||
                        uri.contains("/auth/reset-password") ||
                        uri.equals("/users") ||
                        uri.matches("^/user/\\d+$") ||
                        uri.equals("/me") ||
                        uri.matches("^/user/editar/\\d+$") ||
                        uri.matches("^/user/notification/email/.*$") ||
                        uri.matches("^/user/notificationSubasta/email/.*$") ||
                        uri.equals("/pong")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJwtFromRequest(request);

        if (token != null) {
            try {
                String email = jwtUtil.validateToken(token);
                UserDetails userDetails = new User(email, "", Collections.singletonList(new SimpleGrantedAuthority("user")));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.getWriter().write("Token inválido o expirado");
                return;
            }
        }

        // No token y no es pública => dejar pasar, Spring Security decide
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
