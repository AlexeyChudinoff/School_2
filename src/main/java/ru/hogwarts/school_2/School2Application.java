package ru.hogwarts.school_2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class School2Application {

	public static void main(String[] args) {
		SpringApplication.run(School2Application.class, args);
	}

	@Bean
	CommandLineRunner checkDatabase(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				System.out.println("✅ Database connection SUCCESSFUL");
				System.out.println("✅ URL: " + conn.getMetaData().getURL());
				System.out.println("✅ User: " + conn.getMetaData().getUserName());
			} catch (Exception e) {
				System.out.println("❌ Database connection FAILED: " + e.getMessage());
			}
		};
	}
}