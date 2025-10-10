package page.showmy.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.showmy.model.User;
import page.showmy.model.UserProfile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String name;
    private String title;
    @Size(max = 255, message = "SEO cannot exceed 255 characters")
    private String seo;
    private String email;
    private String resumeUrl;
    private String profilePhoto;
    private String github;
    private String linkedin;
    private String gAnalytics;

    public static UserProfileDTO fromEntities(User user, UserProfile userProfile) {
        if (userProfile == null) {
            return new UserProfileDTO(
                    null,
                    null,
                    null,
                    user.getEmail(), 
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new UserProfileDTO(
                userProfile.getName(),
                userProfile.getTitle(),
                userProfile.getSeo(),
                user.getEmail(),
                userProfile.getResumeUrl(),
                userProfile.getProfilePhoto(),
                userProfile.getGithub(),
                userProfile.getLinkedin(),
                userProfile.getGAnalytics()
        );
    }
}
