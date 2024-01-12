package com.ShortNews.ShortNews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShortNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortNewsApplication.class, args);
	}

}