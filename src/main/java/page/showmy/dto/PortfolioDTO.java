package page.showmy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private UserProfileDTO userProfile;
    private List<ProjectDTO> apps;
    private List<SkillsCategoryDTO> skillsData;
    private List<PublicationDTO> publicationsData;
    private List<WorkExperienceDTO> workExperienceData;
}
