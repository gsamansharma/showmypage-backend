package page.showmy.dto;

import lombok.Data;

@Data
public class UserProfileInputDTO {
    private String name;
    private String title;
    private String resumeUrl;
    private String profilePhoto;
    private String github;
    private String linkedin;
}
