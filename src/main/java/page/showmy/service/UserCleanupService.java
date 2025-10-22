package page.showmy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.showmy.model.User;
import page.showmy.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class UserCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(UserCleanupService.class);

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupUnverifiedUsers() {
        logger.info("Starting nightly cleanup of unverified users...");

        Date now = new Date();
        List<User> usersToClean = userRepository
                .findAllByIsEmailVerifiedFalseAndVerificationTokenExpiryDateBefore(now);

        if (usersToClean.isEmpty()) {
            logger.info("No expired unverified users found. Cleanup complete.");
            return;
        }

        logger.info("Found {} expired unverified user(s). Deleting...", usersToClean.size());

        userRepository.deleteAll(usersToClean);

        logger.info("Successfully deleted {} unverified users. Cleanup complete.", usersToClean.size());
    }
}