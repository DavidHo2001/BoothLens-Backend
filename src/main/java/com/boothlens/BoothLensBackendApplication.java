package com.boothlens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BoothLensBackendApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(BoothLensBackendApplication.class, args);
		
		// This will print after the server starts
		System.out.println("Booth Lens Backend is now running!");

	}

}
