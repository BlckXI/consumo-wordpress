package com.colibrihub.wordpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication

public class ConsumoWordpressApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumoWordpressApplication.class, args);
	}

}
