package page.showmy.dto;

import lombok.Data;
import page.showmy.model.Publication;

@Data
public class PublicationDTO {
    private Long id;
    private String title;
    private String description;
    private String url;

    public static PublicationDTO fromEntity(Publication publication) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setDescription(publication.getDescription());
        dto.setUrl(publication.getUrl());
        return dto;
    }
}
