package page.showmy.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "skills")
@EqualsAndHashCode(exclude = "skills")
@Entity
public class SkillsCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String category;

    @OneToMany(mappedBy = "skillsCategory")
    @JsonManagedReference
    private List<Skill> items;

}

