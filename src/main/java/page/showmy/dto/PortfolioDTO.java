package page.showmy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.showmy.model.Project;
import page.showmy.model.Publication;
import page.showmy.model.WorkExperience;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private UserProfileDTO userProfile;
    private List<Project> apps;
    private List<SkillsCategoryDTO> skillsData;
    private List<Publication> publicationsData;
    private List<WorkExperience> workExperienceData;
}
