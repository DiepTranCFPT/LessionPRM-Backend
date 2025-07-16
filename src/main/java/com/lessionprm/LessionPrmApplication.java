package com.lessionprm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LessionPrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(LessionPrmApplication.class, args);
    }

}