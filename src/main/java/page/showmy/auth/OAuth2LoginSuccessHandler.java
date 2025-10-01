package page.showmy.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import page.showmy.security.JwtUtil;
import page.showmy.security.UserDetailsServiceImpl;

import java.io.IOException;


@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final String frontendUrl;
    private final UserDetailsServiceImpl userDetailsService;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, @Value("${frontend.url}") String frontendUrl, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails);
        response.sendRedirect(frontendUrl + "/login/success?token=" + token);
    }
}
