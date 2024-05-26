package ru.utlc.referencedataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ReferenceDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReferenceDataApplication.class, args);
	}

}
