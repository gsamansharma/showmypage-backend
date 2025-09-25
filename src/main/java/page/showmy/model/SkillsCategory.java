package page.showmy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class SkillsCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String category;

    @OneToMany(mappedBy = "skillsCategory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Skill> items;

}

