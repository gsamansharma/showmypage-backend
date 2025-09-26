package page.showmy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import page.showmy.model.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
