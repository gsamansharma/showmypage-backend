package page.showmy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublicationInputDTO {
    @NotBlank
    private String title;
    private String description;
    private String url;
}
