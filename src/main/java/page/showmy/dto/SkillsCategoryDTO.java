package page.showmy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.showmy.model.SkillsCategory;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillsCategoryDTO {
    private Long id;
    private String category;
    private List<SkillDTO> items;

    public static SkillsCategoryDTO fromEntity(SkillsCategory skillsCategory) {
        List<SkillDTO> skillDTOs = skillsCategory.getItems().stream()
                .map(SkillDTO::fromEntity)
                .collect(Collectors.toList());
        return new SkillsCategoryDTO(skillsCategory.getId(), skillsCategory.getCategory(), skillDTOs);
    }
}