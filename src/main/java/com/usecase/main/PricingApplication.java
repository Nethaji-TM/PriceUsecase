package com.usecase.main;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.usecase.model.Price;
import com.usecase.repository.PriceRepository;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(scanBasePackages = "com.usecase")
@EntityScan(basePackages = "com.usecase.model")
@EnableJpaRepositories(basePackages = "com.usecase.repository")
@OpenAPIDefinition(info = @Info(title = "Pricing API", version = "1.0", description = "API for managing prices"))
public class PricingApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(PricingApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(PricingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PriceRepository priceRepository) {
        return args -> {
            if (priceRepository.findAll().isEmpty()) {
                List<Price> prices = List.of(
                    new Price("7001", "1000102674", "retail", "regular", "CAD", 30.0, Instant.parse("2023-12-31T23:59:59Z"), Instant.parse("9999-12-31T23:59:59Z")),
                    new Price("7001", "1000102674", "retail", "discounted", "CAD", 27.0, Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-31T23:59:58Z")),
                    new Price("7001", "1000102674", "retail", "discounted", "CAD", 26.5, Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-25T23:59:58Z"))
                );

                priceRepository.saveAll(prices);
                LOGGER.info("Initial data inserted successfully.");
            } else {
            	LOGGER.info("Data already exists in the database.");
            }
        };
    }
}