package com.github.sol239.javafi.backend;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(DBHandler dbHandler) {
		return args -> {
			try {
				// Můžete zde volat metody dbHandler, např. dbHandler.isConnected();
			} catch (Exception e) {
				System.out.println("Error connecting to the database: " + e.getMessage());
			}
		};
	}

}
