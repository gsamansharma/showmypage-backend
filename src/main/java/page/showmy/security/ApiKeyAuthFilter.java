package page.showmy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${backend.api.secret}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader("X-Forwarded-Secret");

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (expectedApiKey != null && !expectedApiKey.isEmpty() && expectedApiKey.equals(requestApiKey)) {
                ServerToServerAuthenticationToken auth = new ServerToServerAuthenticationToken(Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.setAttribute("isServerToServer", true);
            }
        }

        // Continue the filter chain.
        filterChain.doFilter(request, response);
    }
}
