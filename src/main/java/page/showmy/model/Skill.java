package page.showmy.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"skillscategory", "projects", "workexperiences"})
@EqualsAndHashCode(exclude ={"skillsCategory", "projects", "workExperiences"} )
@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String icon;

    @ManyToOne
    @JoinColumn(name = "skills_category_id")
    @JsonBackReference
    private SkillsCategory skillsCategory;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<User> users =  new HashSet<>();
}
