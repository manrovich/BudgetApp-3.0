package ru.manrovich.cashflow.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.manrovich.cashflow")
@EnableJpaRepositories(basePackages = "ru.manrovich.cashflow")
@EntityScan(basePackages = "ru.manrovich.cashflow")
public class BudgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetApplication.class, args);
	}

}