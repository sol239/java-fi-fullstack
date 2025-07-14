package com.github.sol239.javafi.backend;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
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
	CommandLineRunner init(DBHandler dbHandler, InstrumentExecutor instrumentExecutor) {
		return args -> {
			try {
				System.out.println("Connected");
				int count = instrumentExecutor.getInstrumentCount();
				System.out.println("Počet instrumentů: " + count);
			} catch (Exception e) {
				System.out.println("Error connecting to the database: " + e.getMessage());
			}
		};
	}

}
