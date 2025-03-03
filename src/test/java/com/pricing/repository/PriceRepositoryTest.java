package com.pricing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.usecase.main.PricingApplication;
import com.usecase.model.Price;
import com.usecase.repository.PriceRepository;

@SpringBootTest(classes = PricingApplication.class)
@DataJpaTest
public class PriceRepositoryTest {
	@Autowired
    private PriceRepository priceRepository;

    private Price price1, price2;

    @BeforeEach
    void setUp() {
        price1 = new Price("7001", "1000102674", "retail", "regular", "CAD", 30.0,
                Instant.parse("2023-12-31T23:59:59Z"), Instant.parse("9999-12-31T23:59:59Z"));
        price2 = new Price("7001", "1000102674", "retail", "discounted", "CAD", 27.0,
                Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-31T23:59:58Z"));

        priceRepository.saveAll(List.of(price1, price2));
    }

    @Test
    void testFindByStoreIDAndArticleID() {
        List<Price> prices = priceRepository.findByStoreIDAndArticleID("7001", "1000102674");
        assertEquals(2, prices.size());
    }
}
