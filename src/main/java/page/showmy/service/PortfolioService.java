package page.showmy.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import page.showmy.auth.dto.PortfolioDTO;
import page.showmy.auth.dto.UserProfileDTO;
import page.showmy.model.SkillsCategory;
import page.showmy.model.User;
import page.showmy.repository.SkillsCategoryRepository;
import page.showmy.repository.UserRepository;

import java.util.List;

@Service
public class PortfolioService {
    private final UserRepository userRepository;
    private final SkillsCategoryRepository skillsCategoryRepository;


    public PortfolioService(UserRepository userRepository, SkillsCategoryRepository skillsCategoryRepository) {
        this.userRepository=userRepository;
        this.skillsCategoryRepository=skillsCategoryRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found: "+username));
        UserProfileDTO userProfileDTO = UserProfileDTO.fromEntities(user, user.getUserProfile());

        List<SkillsCategory> allSkills = skillsCategoryRepository.findAll();

        return new PortfolioDTO(
                userProfileDTO,
                user.getProjects(),
                allSkills,
                user.getPublications()
        );
    }
}
