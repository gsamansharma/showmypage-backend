package page.showmy.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import page.showmy.model.*;
import page.showmy.service.PortfolioService;
import java.util.List;

@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @QueryMapping
    public User portfolio(@Argument String username) {
        return portfolioService.getPortfolioByUsername(username);
    }


    @SchemaMapping(typeName = "Portfolio", field = "user")
    public User getUserInfo(User user) {
        return user;
    }

    @SchemaMapping(typeName = "Portfolio", field = "apps")
    public List<Project> getApps(User user) {
        return user.getProjects();
    }

    @SchemaMapping(typeName = "Portfolio", field = "publicationsData")
    public List<Publication> getPublicationsData(User user) {
        return user.getPublications();
    }

    @SchemaMapping(typeName = "Portfolio", field = "skillsData")
    public List<SkillsCategory> getSkillsData(User user) {
        return user.getSkillsCategories();
    }
}
