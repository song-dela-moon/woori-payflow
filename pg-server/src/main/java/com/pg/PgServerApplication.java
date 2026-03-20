package com.pg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PgServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgServerApplication.class, args);
	}

}
