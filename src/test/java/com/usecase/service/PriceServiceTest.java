package com.usecase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.usecase.main.PricingApplication;
import com.usecase.model.Price;
import com.usecase.repository.PriceRepository;

@SpringBootTest(classes = PricingApplication.class)
@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PriceServiceTest.class);

	@Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Price price1, price2, price3;
    private String storeID = "7001";
    private String articleID = "1000102674";

    @BeforeEach
    void setUp() {
        price1 = new Price(storeID, articleID, "retail", "discounted", "CAD", 27.0,
                Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-31T23:59:58Z"));

        price2 = new Price(storeID, articleID, "retail", "discounted", "CAD", 27.0,
                Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2025-12-31T23:59:58Z"));

        price3 = new Price(storeID, articleID, "retail", "regular", "CAD", 30.0,
                Instant.parse("2023-12-31T23:59:59Z"), Instant.parse("9999-12-31T23:59:59Z"));
    }

    @Test
    void testGetPrices_CacheMiss_DBHit() {
        when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(List.of(price1, price3));

        Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);

        assertNotNull(response);
        assertEquals(articleID, response.get("article"));
        assertEquals(storeID, response.get("store"));
        assertEquals(2, ((List<?>) response.get("prices")).size());
    }

    @Test
    void testGetPrices_NotFound() {
        when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(List.of());

        Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);
        assertNull(response);
    }

    @Test
    void testMergeAndMarkOverlappingPrices() {
        List<Price> mergedPrices = priceService.mergeAndMarkOverlappingPrices(List.of(price1, price2, price3));

        assertEquals(2, mergedPrices.size()); // Two price groups after merging
        assertEquals(27.0, mergedPrices.get(0).getAmount()); // Merged price should have the same amount
    }
}
