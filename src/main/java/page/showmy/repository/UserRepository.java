package page.showmy.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import page.showmy.model.User;

import java.util.Optional;
import java.util.List;
import java.util.Date;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "user-with-all-details")
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @EntityGraph(value = "user-with-all-details")
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findAllByIsEmailVerifiedFalseAndVerificationTokenExpiryDateBefore(Date now);

    @Modifying
    @Query("DELETE FROM User u WHERE u.isEmailVerified = false AND u.verificationTokenExpiryDate < :now")
    void deleteUnverifiedExpiredUsers(@Param("now") Date now);
}
