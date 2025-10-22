package page.showmy.dto;

import lombok.Data;
import page.showmy.model.Project;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String liveUrl;
    private String githubUrl;
    private String videoUrl;
    private Set<String> imageUrls;
    private Set<SkillDTO> skills;

    public static ProjectDTO fromEntity(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setIcon(project.getIcon());
        dto.setLiveUrl(project.getLiveUrl());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setVideoUrl(project.getVideoUrl());
        dto.setImageUrls(new HashSet<>(project.getImageUrls()));
        dto.setSkills(project.getSkills().stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toSet()));
        return dto;
    }
}
