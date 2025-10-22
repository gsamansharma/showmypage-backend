package page.showmy.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"userProfile","projects", "publications", "skills", "workExperiences","topSkills"})
@EqualsAndHashCode(exclude = {"userProfile", "projects", "publications", "skills", "workExperiences","topSkills"})
@NamedEntityGraph(
        name = "user-with-all-details",
        attributeNodes = {
                @NamedAttributeNode("userProfile"),
                @NamedAttributeNode(value = "projects", subgraph = "project-skills"),
                @NamedAttributeNode(value = "skills", subgraph = "skill-category"),
                @NamedAttributeNode("publications"),
                @NamedAttributeNode(value = "workExperiences", subgraph = "work-experience-skills"),
                @NamedAttributeNode(value = "topSkills")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "project-skills",
                        attributeNodes = {
                                @NamedAttributeNode("skills")
                        }
                ),
                @NamedSubgraph(
                        name = "skill-category",
                        attributeNodes = {
                                @NamedAttributeNode("skillsCategory")
                        }
                ),
                @NamedSubgraph(
                        name = "work-experience-skills",
                        attributeNodes = {
                                @NamedAttributeNode("skills")
                        }
                )
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(unique = true)
    private String resetToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date resetTokenExpiryDate;

    @Column(nullable = false)
    private Boolean isEmailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Publication> publications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<WorkExperience> workExperiences = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills =  new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_top_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> topSkills = new HashSet<>();

    public void addProject(Project project) {
        this.projects.add(project);
        project.setUser(this);
    }

    public void addPublication(Publication publication) {
        this.publications.add(publication);
        publication.setUser(this);
    }

    public void addWorkExperience(WorkExperience workExperience) {
        this.workExperiences.add(workExperience);
        workExperience.setUser(this);
    }
}
