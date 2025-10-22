package page.showmy.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import page.showmy.model.User;
import page.showmy.repository.UserRepository;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException{
        User user = userRepository.findByUsernameIgnoreCase(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)));

        String password = user.getPassword() != null ? user.getPassword() : "";
        if (!user.getIsEmailVerified()) {
            throw new DisabledException("User account is not yet verified. Please check your email.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), password, new ArrayList<>());
    }
}
