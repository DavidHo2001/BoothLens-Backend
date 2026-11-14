package com.boothlens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BoothLensBackendApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(BoothLensBackendApplication.class, args);

	}

}
