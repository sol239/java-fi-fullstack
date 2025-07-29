package com.github.sol239.javafi.backend;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application class for the JavaFI backend.
 * This class is responsible for starting the Spring Boot application and initializing the database.
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Main method to run the Spring Boot application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	/**
	 * CommandLineRunner bean to initialize the database.
	 * @param dbHandler the database handler for initializing the database
	 * @param instrumentExecutor the instrument executor for executing instruments
	 * @return CommandLineRunner that initializes the database
	 */
	@Bean
	CommandLineRunner init(DBHandler dbHandler, InstrumentExecutor instrumentExecutor) {
		return args -> {
			DBHandler.dataInit();
		};
	}

}
