package page.showmy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.showmy.model.SkillsCategory;

public interface SkillsCategoryRepository extends JpaRepository<SkillsCategory, Long> {
}
