package page.showmy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShowMyPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShowMyPageApplication.class, args);
    }

}
