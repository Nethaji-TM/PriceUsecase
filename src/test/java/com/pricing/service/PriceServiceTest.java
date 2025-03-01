package com.pricing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

import com.usecase.model.Price;
import com.usecase.repository.PriceRepository;
import com.usecase.service.PriceService;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PriceServiceTest.class);

	@Mock
	private PriceRepository priceRepository; // Mock repository

	@InjectMocks
	private PriceService priceService; // Inject mocked repo into service

	private Price price1, price2, price3;
	private String storeID = "7001";
	private String articleID = "1000102674";

	@BeforeEach
	void setUp() {
		price1 = new Price("7001", "1000102674", "retail", "discounted", "CAD", 27.0, Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-31T23:59:58Z"));
		price2 = new Price("7002", "1000102674", "retail", "discounted", "CAD", 26.5,
				Instant.parse("2023-12-21T23:59:59Z"), Instant.parse("2025-12-25T23:59:58Z"));
		price3 = new Price("7003", "1000102674", "retail","regular", "CAD", 30.0,
				Instant.parse("2023-12-31T23:59:59Z"), Instant.parse("9999-12-31T23:59:59Z"));
	}

	@Test
	void testGetPrices_CacheHit() {
		LOGGER.info("testGetPrices_CacheHit ENTRY", PriceServiceTest.class);
		Map<String, List<Price>> cache = new HashMap<>();
		cache.put(storeID + "-" + articleID, List.of(price1, price3));

		Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);
		LOGGER.info("Resp : " + response.size(), PriceServiceTest.class);

		assertNotNull(response);
		assertEquals(articleID, response.get("article"));
		assertEquals(storeID, response.get("store"));
		assertEquals(2, ((List<?>) response.get("prices")).size()); // Expecting 2 prices
		LOGGER.info("testGetPrices_CacheHit EXIT", PriceServiceTest.class);
	}

	@Test
	void testGetPrices_CacheMiss_DBHit() {
		LOGGER.info("testGetPrices_CacheMiss_DBHit ENTRY", PriceServiceTest.class);
		when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(List.of(price1, price3));

		Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);
		LOGGER.info("Resp : " + response.size(), PriceServiceTest.class);
		assertNotNull(response);
		assertEquals(2, ((List<?>) response.get("prices")).size());
		LOGGER.info("testGetPrices_CacheMiss_DBHit EXIT", PriceServiceTest.class);
	}

	@Test
	void testGetPrices_NotFound() {
		LOGGER.info("testGetPrices_NotFound ENTRY", PriceServiceTest.class);
		when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(Collections.emptyList());

		Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);
		LOGGER.info("Resp " + response.size(), PriceServiceTest.class);
		assertNull(response);
		//LOGGER.info("testGetPrices_NotFound EXIT", PriceServiceTest.class);
	}

	@Test
	void testPagination_ValidPage() {
		LOGGER.info("testPagination_ValidPage ENTRY", PriceServiceTest.class);
		when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(List.of(price1, price2, price3));

		Map<String, Object> response = priceService.getPrices(storeID, articleID, 1, 2);
		LOGGER.info("Resp " + response.size(), PriceServiceTest.class);
		assertNotNull(response);
		List<?> prices = (List<?>) response.get("prices");
		assertEquals(2, prices.size()); // First page should return 2 items
		LOGGER.info("testPagination_ValidPage EXIT", PriceServiceTest.class);
	}

	@Test
	void testPagination_OutOfRange() {
		LOGGER.info("testPagination_OutOfRange ENTRY", PriceServiceTest.class);
		when(priceRepository.findByStoreIDAndArticleID(storeID, articleID)).thenReturn(List.of(price1, price2, price3));

		Map<String, Object> response = priceService.getPrices(storeID, articleID, 3, 2);
		LOGGER.info("Resp " + response.size(), PriceServiceTest.class);
		assertNull(response);
		//LOGGER.info("testPagination_OutOfRange EXIT", PriceServiceTest.class);
	}

	@Test
	void testMergeAndMarkOverlappingPrices() {
		LOGGER.info("testMergeAndMarkOverlappingPrices ENTRY", PriceServiceTest.class);
		List<Price> prices = Arrays.asList(price1, price2, price3);
		List<Price> processedPrices = priceService.mergeAndMarkOverlappingPrices(prices);

		assertEquals(2, processedPrices.size()); // Two unique price groups
		assertEquals(27.0, processedPrices.get(0).getAmount()); // Merged price should have same amount
		assertEquals(Instant.parse("2023-12-21T23:59:59Z"), processedPrices.get(0).getValidFrom());
		assertEquals(Instant.parse("2025-12-31T23:59:58Z"), processedPrices.get(0).getValidTo());

		// Check overlap flag
		assertFalse(processedPrices.get(0).isOverlapped()); // Merged price should not be overlapped
		assertFalse(processedPrices.get(1).isOverlapped()); // Regular price should not be overlapped
		LOGGER.info("EXIT", PriceServiceTest.class);
	}
}
