package page.showmy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import page.showmy.model.Skill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private Long id;
    private String name;
    private String icon;

    public static SkillDTO fromEntity(Skill skill) {
        return new SkillDTO(skill.getId(), skill.getName(), skill.getIcon());
    }
}
