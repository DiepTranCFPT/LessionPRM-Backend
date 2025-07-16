package com.lessionprm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LessionPrmBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LessionPrmBackendApplication.class, args);
	}

}