package page.showmy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Set;

@Data
public class ProjectInputDTO {
    @NotBlank(message = "Project name cannot be blank")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    private String name;

    @NotBlank(message =  "Project description cannot be blank")
    private String description;

    @URL(message = "Live URL must be a valid URL")
    private String icon;

    @URL(message = "Live URL must be a valid URL")
    private String liveUrl;

    @URL(message = "Github URL must be a valid URL")
    private String githubUrl;

    @URL(message = "Video URL must be a valid URL")
    private String videoUrl;

    private List<String> imageUrls;
    private Set<Long> skillIds;
}
