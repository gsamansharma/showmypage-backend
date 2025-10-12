package page.showmy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String url;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;
}
