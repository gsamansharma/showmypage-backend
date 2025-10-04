package page.showmy.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import page.showmy.model.AuthProvider;
import page.showmy.model.User;
import page.showmy.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return processOAuthUser(oAuth2User, registrationId);
    }


    public OAuth2User processOAuthUser(OAuth2User oAuth2User, String registrationId) {
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from " + registrationId + ". Please ensure your account has a valid email.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return oAuth2User;
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        System.out.println(attr.getRequest());
        HttpSession session = attr.getRequest().getSession(false);
        String username = (String) session.getAttribute("OAUTH2_USERNAME");
        System.out.println(username);
        if (username == null) {
            username = generateUniqueUsernameFromEmail(email);
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setAuthProvider(AuthProvider.valueOf(registrationId));
        newUser.setPassword(null);
        newUser.setIsEmailVerified(true);
        userRepository.save(newUser);

        return oAuth2User;
    }

    private String generateUniqueUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf("@"));
        String username = baseUsername;
        int counter = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }
}