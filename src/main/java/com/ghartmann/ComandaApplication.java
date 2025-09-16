package com.ghartmann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication 
@EntityScan(basePackages = "com.ghartmann.domain") 
@EnableJpaRepositories(basePackages = "com.ghartmann.repository")
public class ComandaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComandaApplication.class, args);
	}

}
