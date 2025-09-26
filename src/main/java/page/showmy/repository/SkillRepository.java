package page.showmy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.showmy.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
