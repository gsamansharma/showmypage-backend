package page.showmy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String icon;
    private String liveUrl;
    private String githubUrl;
    private String videoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_images", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    @JsonIgnore
    private User user;
}
