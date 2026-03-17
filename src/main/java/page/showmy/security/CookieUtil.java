package page.showmy.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;

@Component
public class CookieUtil {
    private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    private final String studioUrl;
    private final String activeProfile;

    public CookieUtil(@Value("${studio.url}") String studioUrl,
                      @Value("${spring.profiles.active:prod}") String activeProfile) {
        this.studioUrl = studioUrl;
        this.activeProfile = activeProfile;
    }

    public void addJwtCookie(HttpServletResponse response, String token) {
        String domain = getRootDomain(studioUrl);

        // Default to production settings
        boolean secure = true;
        String sameSite = "Lax";

        // Automatically downgrade for local development
        if (studioUrl != null && studioUrl.startsWith("http://")) {
            secure = false;
        }

        logger.info("Setting auth_token cookie. studioUrl: {}, domain: {}, secure: {}, sameSite: {}, activeProfile: {}",
                studioUrl, domain, secure, sameSite, activeProfile);

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from("auth_token", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite(sameSite);

        if (domain != null && !domain.isEmpty()) {
            cookieBuilder.domain(domain);
        }

        ResponseCookie cookie = cookieBuilder.build();
        String cookieString = cookie.toString();
        logger.info("Cookie header value: {}", cookieString);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieString);
    }

    private String getRootDomain(String url) {
        if (url == null)
            return null;
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null)
                return null;

            if (host.equals("localhost") || host.equals("127.0.0.1") || host.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
                return null;
            }

            String[] parts = host.split("\\.");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
