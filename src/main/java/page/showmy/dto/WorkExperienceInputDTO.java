package page.showmy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class WorkExperienceInputDTO {
    @NotBlank
    private String jobTitle;

    @NotBlank
    private String companyName;

    private String companyLogoUrl;
    private String location;

    @NotBlank(message = "Start date cannot be blank and must be in yyyy-MM format")
    private String startDate;

    @NotBlank(message = "Start date cannot be blank and must be in yyyy-MM format")
    private String endDate;
    private String description;
    private Set<Long> skillIds;
}
