package com.cloudbeds.userfakefeeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class UserFakeFeederApplication implements CommandLineRunner {

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(UserFakeFeederApplication.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Joining thread, you can press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }
}
