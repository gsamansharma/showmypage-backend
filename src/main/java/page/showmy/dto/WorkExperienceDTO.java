package page.showmy.dto;

import lombok.Data;
import page.showmy.model.WorkExperience;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class WorkExperienceDTO {
    private Long id;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private Date startDate;
    private Date endDate;
    private String description;
    private Set<SkillDTO> skills = new HashSet<>();

    public static WorkExperienceDTO fromEntity(WorkExperience workExperience) {
        WorkExperienceDTO dto = new WorkExperienceDTO();
        dto.setId(workExperience.getId());
        dto.setJobTitle(workExperience.getJobTitle());
        dto.setCompanyName(workExperience.getCompanyName());
        dto.setCompanyLogoUrl(workExperience.getCompanyLogoUrl());
        dto.setLocation(workExperience.getLocation());
        dto.setStartDate(workExperience.getStartDate());
        dto.setEndDate(workExperience.getEndDate());
        dto.setDescription(workExperience.getDescription());
        dto.setSkills(workExperience.getSkills().stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toSet()));
        return dto;
    }
}
