package page.showmy.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import page.showmy.dto.PortfolioDTO;
import page.showmy.dto.UserProfileDTO;
import page.showmy.service.PortfolioService;

import java.util.List;

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
    public UserProfileDTO user(@Argument String username) {
        return portfolioService.getUserProfileByUsername(username);
    }
}
