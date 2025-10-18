package page.showmy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import page.showmy.auth.CustomOAuth2UserService;
import page.showmy.auth.GitHubEmailEnricher;
import page.showmy.auth.HttpCookieOAuth2AuthorizationRequestRepository;
import page.showmy.auth.OAuth2LoginSuccessHandler;
import page.showmy.security.JwtRequestFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CustomOAuth2UserService customOAuth2UserService, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;

    }

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthorizationManager<RequestAuthorizationContext> internalOnly = (authentication, context) -> {
            IpAddressMatcher dockerRange1 = new IpAddressMatcher("172.16.0.0/12");
            IpAddressMatcher dockerRange2 = new IpAddressMatcher("192.168.0.0/16");
            IpAddressMatcher localhost = new IpAddressMatcher("127.0.0.1");
            IpAddressMatcher localhostV6 = new IpAddressMatcher("::1");

            boolean isInternal = localhost.matches(context.getRequest()) ||
                    localhostV6.matches(context.getRequest()) ||
                    dockerRange1.matches(context.getRequest()) ||
                    dockerRange2.matches(context.getRequest());

            return new AuthorizationDecision(isInternal);
        };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**", "/api/health").permitAll()
                        .requestMatchers("/graphql", "/graphiql").access((authentication, context) -> {
                            String remoteAddr = context.getRequest().getRemoteAddr();
                            boolean isInternal = remoteAddr.equals("127.0.0.1") || remoteAddr.equals("::1") ||
                                    remoteAddr.startsWith("172.") || remoteAddr.startsWith("192.168.") || remoteAddr.startsWith("10.");
                            return new org.springframework.security.authorization.AuthorizationDecision(isInternal);
                        })
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                                .authorizationEndpoint(endpoint -> endpoint
                                    .baseUri("/oauth2/authorize")
                                    .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        )
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService())
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        GitHubEmailEnricher gitHubEmailEnricher = new GitHubEmailEnricher();
        DefaultOAuth2UserService googleUserService = new DefaultOAuth2UserService();
        return request -> {
            String registrationId = request.getClientRegistration().getRegistrationId();
            OAuth2User oAuth2User;
            if ("github".equalsIgnoreCase(registrationId)) {
                oAuth2User = gitHubEmailEnricher.loadUser(request);
            }
            else {
                oAuth2User = googleUserService.loadUser(request);
            }
                return customOAuth2UserService.processOAuthUser(oAuth2User, request, registrationId);

        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
