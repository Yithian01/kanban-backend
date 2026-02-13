package com.example.spring_boot_jwt_boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringBootJwtBoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJwtBoilerplateApplication.class, args);
	}

}
