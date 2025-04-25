package com.usecase.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.usecase.dto.PriceRequest;
import com.usecase.model.Price;
import com.usecase.repository.PriceRepository;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class PriceService {
	@Autowired
	private PriceRepository priceRepository;
	
	@Autowired
	private SqsAsyncClient sqsAsyncClient;
	
	private final Map<String, List<Price>> cache = new HashMap<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(PriceService.class);
	private final WebClient webClient = WebClient.create("https://your-api-gateway.amazonaws.com/price");
	
	public Map<String, Object> getPrices(String storeID, String articleID, int page, int pageSize) {
		LOGGER.info("getPrices INIT");
		String cacheKey = storeID + "-" + articleID;
		List<Price> prices = cache.computeIfAbsent(cacheKey,
				k -> priceRepository.findByStoreIDAndArticleID(storeID, articleID));

		if (prices.isEmpty()) {
			LOGGER.info("getPrices EXIT: Prices not found in cache");
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No prices were found
			// for a given request");
			return null;
		}

		prices = mergeAndMarkOverlappingPrices(prices);
		LOGGER.info("Prices found in cache " + prices);
		int start = (page - 1) * pageSize;
		int end = Math.min(start + pageSize, prices.size());
		if (start >= prices.size()) {
			LOGGER.info("getPrices EXIT: Page out of range");
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page out of range");
			return null;
		}
		LOGGER.info("getPrices EXIT");
		return Map.of("generated_date", Instant.now().toString(), "article", articleID, "store", storeID, "meta",
				Map.of("page", page, "size", pageSize), "properties", Map.of("uom", "EA", "description",
						"WH Halifax Passage Lever in Satin Nickel", "brand", "Weiser", "model", "9GLA1010"),
				"prices", prices.subList(start, end));
	}

	public List<Price> mergeAndMarkOverlappingPrices(List<Price> prices) {
		LOGGER.info("mergeAndMarkOverlappingPrices INIT prices size: " + prices.size());
		prices.sort(Comparator.comparing(Price::getValidFrom));
		List<Price> mergedPrices = new ArrayList<>();
		for (Price price : prices) {
			if (!mergedPrices.isEmpty()) {
				Price last = mergedPrices.get(mergedPrices.size() - 1);
				if (last.getValidTo().isAfter(price.getValidFrom()) && last.getAmount().equals(price.getAmount())) {
					last.setValidTo(
							last.getValidTo().isAfter(price.getValidTo()) ? last.getValidTo() : price.getValidTo());
					continue;
				} else if (last.getValidTo().isAfter(price.getValidFrom())
						&& !last.getAmount().equals(price.getAmount())) {
					price.setOverlapped(true);
				}
			}
			mergedPrices.add(price);
		}
		LOGGER.info("mergeAndMarkOverlappingPrices EXIT");
		return mergedPrices;
	}
	
	public List<Price> findAll() {
		LOGGER.info("Fetching all prices");
		return priceRepository.findAll();
	}
	
	public Price addPrice(PriceRequest priceRequest) {
		LOGGER.info("Add price");
		Price price = new Price();
		price.setStoreID(priceRequest.storeID());
		price.setArticleID(priceRequest.articleID());
		price.setType(priceRequest.type());
		price.setSubtype(priceRequest.subtype());
		price.setCurrency(priceRequest.currency());
		price.setAmount(priceRequest.amount());
		price.setValidFrom(priceRequest.validFrom());
		price.setValidTo(priceRequest.validTo());
		price.setOverlapped(priceRequest.overlapped());
		
		//sendPriceUpdate(priceRequest.toString());
		
		return priceRepository.saveAndFlush(price);
	}

	public String getDynamicPrice(String productId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/").queryParam("productId", productId).build())
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}
	
	public void sendPriceUpdate(String message) {
		LOGGER.info("sendPriceUpdate price using queue");
		SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl("https://sqs.eu-north-1.amazonaws.com/509399625873/MyQueue.fifo")
                .messageBody(message)
                .build();

        sqsAsyncClient.sendMessage(sendMessageRequest);
    }

}
