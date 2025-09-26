package page.showmy.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import page.showmy.auth.dto.PortfolioDTO;
import page.showmy.service.PortfolioService;

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
}
