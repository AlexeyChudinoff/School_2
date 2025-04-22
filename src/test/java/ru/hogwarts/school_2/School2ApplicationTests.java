package ru.hogwarts.school_2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

// Аннотация @SpringBootApplication означает, что этот класс является точкой входа Spring Boot приложения
@SpringBootApplication
class School2Application {

	// Метод main запускает приложение
	public static void main(String[] args) {
		SpringApplication.run(School2Application.class, args);
	}
}

// Отдельный тестовый класс для проверки загрузки контекста приложения
@SpringBootTest(classes = School2Application.class)
class School2ApplicationTests {

	// Тест проверяет успешность запуска приложения и загрузки всех компонентов
	@Test
	void contextLoads() {}
}













//package ru.hogwarts.school_2;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class School2ApplicationTests {
//
//	@Test
//public	void contextLoads() {
//	}
//
//}
