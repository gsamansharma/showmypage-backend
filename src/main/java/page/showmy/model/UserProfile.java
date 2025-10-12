package page.showmy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    private String seo;

    @Column(nullable = false)
    private String github;

    @Column(nullable = false)
    private String linkedin;

    @Column(nullable = false)
    private String profilePhoto;

    @Column(nullable = false)
    private String resumeUrl;

    private String gAnalytics;
}
