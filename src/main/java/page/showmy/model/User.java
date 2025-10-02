package page.showmy.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"userProfile","projects", "publications", "skills"})
@EqualsAndHashCode(exclude = {"userProfile", "projects", "publications", "skills"})
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Publication> publications = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills =  new HashSet<>();

    public void addProject(Project project) {
        this.projects.add(project);
        project.setUser(this);
    }

    public void addPublication(Publication publication) {
        this.publications.add(publication);
        publication.setUser(this);
    }
}
