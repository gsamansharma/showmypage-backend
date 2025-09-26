package page.showmy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.showmy.model.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByUser_Username(String username);
}
