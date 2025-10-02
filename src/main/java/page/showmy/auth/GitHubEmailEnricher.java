package page.showmy.auth;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitHubEmailEnricher implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    private final RestTemplate restTemplate = new RestTemplate();


    @Override

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User user = delegate.loadUser(userRequest);
        String token = userRequest.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        List<Map<String, Object>> emails = response.getBody();

        String primaryEmail = null;
        for (Map<String, Object> emailEntry : emails) {
            if (Boolean.TRUE.equals(emailEntry.get("primary"))) {
                primaryEmail = (String) emailEntry.get("email");
                break;
            }
        }

        Map<String, Object> merged = new HashMap<>(user.getAttributes());
        merged.put("email", primaryEmail);
        return new DefaultOAuth2User(user.getAuthorities(), merged, "login");

    }

}