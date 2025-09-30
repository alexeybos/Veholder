package org.skillsmart.veholder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VeholderApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeholderApplication.class, args);
	}

}
