package page.showmy.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import page.showmy.dto.*;
import page.showmy.model.Project;
import page.showmy.model.Publication;
import page.showmy.model.Skill;
import page.showmy.model.WorkExperience;
import page.showmy.service.PortfolioService;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @QueryMapping
    public PortfolioDTO portfolio(@Argument String username) {
        return portfolioService.getPortfolioByUsername(username);
    }

    @QueryMapping
    public List<UserProfileDTO> users(){
        return portfolioService.getAllUsers();
    }

    @QueryMapping
    public List<SkillsCategoryDTO> allSkills() {
        return portfolioService.getAllSkills();
    }

    @QueryMapping
    public UserProfileDTO user(@Argument String username) {
        return portfolioService.getUserProfileByUsername(username);
    }

    @MutationMapping
    public UserProfileDTO updateUserProfile(@Argument UserProfileInputDTO profileInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.updateUserProfile(username, profileInput);
    }

    @MutationMapping
    public Project addProject(@Argument ProjectInputDTO projectInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.addProject(username, projectInput);
    }

    @MutationMapping
    public Project updateProject(@Argument Long projectId, @Argument ProjectInputDTO projectInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.updateProject(projectId, username, projectInput);
    }

    @MutationMapping
    public boolean deleteProject(@Argument Long projectId, Principal principal) {
        String username = principal.getName();
        return portfolioService.deleteProject(projectId, username);
    }

    @MutationMapping
    public Publication addPublication(@Argument PublicationInputDTO publicationInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.addPublication(username, publicationInput);
    }

    @MutationMapping
    public Publication updatePublication(@Argument Long publicationId, @Argument PublicationInputDTO publicationInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.updatePublication(publicationId, username, publicationInput);
    }

    @MutationMapping
    public boolean deletePublication(@Argument Long publicationId, Principal principal) {
        String username = principal.getName();
        return portfolioService.deletePublication(publicationId, username);
    }

    @MutationMapping
    public Set<Skill> updateUserSkills(@Argument List<Long> skillIds, Principal principal){
        String username = principal.getName();
        return portfolioService.updateUserSkills(username, skillIds);
    }

    @MutationMapping
    public WorkExperience addWorkExperience(@Argument WorkExperienceInputDTO workExperienceInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.addWorkExperience(username, workExperienceInput);
    }

    @MutationMapping
    public WorkExperience updateWorkExperience(@Argument Long experienceId, @Argument WorkExperienceInputDTO workExperienceInput, Principal principal) {
        String username = principal.getName();
        return portfolioService.updateWorkExperience(experienceId, username, workExperienceInput);
    }

    @MutationMapping
    public boolean deleteWorkExperience(@Argument Long experienceId, Principal principal) {
        String username = principal.getName();
        return portfolioService.deleteWorkExperience(experienceId, username);
    }
}
