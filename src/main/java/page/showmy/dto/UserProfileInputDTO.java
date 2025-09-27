package page.showmy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UserProfileInputDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String title;

    @NotBlank
    @URL(message = "Resume url must a valid url")
    private String resumeUrl;

    @NotBlank
    @URL(message = "Profile Photo Link must be a valid url")
    private String profilePhoto;

    @NotBlank
    @URL(message = "Github account must be a valid url")
    private String github;

    @NotBlank
    @URL(message = "Linkedin account must be a valid url")
    private String linkedin;
}
