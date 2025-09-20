package page.showmy.service;

import org.springframework.stereotype.Service;
import page.showmy.model.User;
import page.showmy.repository.UserRepository;

@Service
public class PortfolioService {
    private final UserRepository userRepository;

    public PortfolioService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public User getPortfolioByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
