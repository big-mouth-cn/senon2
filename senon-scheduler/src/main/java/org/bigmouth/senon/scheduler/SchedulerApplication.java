package org.bigmouth.senon.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author allen
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories("org.bigmouth.senon")
@EntityScan("org.bigmouth.senon")
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
