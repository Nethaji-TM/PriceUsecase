package com.usecase.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.usecase.handler.PriceNotFoundException;
import com.usecase.model.Price;
import com.usecase.service.PriceService;

@RestController
@RequestMapping("/pricing/v1")
public class PricingController {
	@Autowired
	private PriceService priceService;
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingController.class);

	@GetMapping("/prices/{storeID}/{articleID}")
	public Map<String, Object> getPrices(@PathVariable String storeID, @PathVariable String articleID,
			@RequestParam int page, @RequestParam int pageSize) {
		
		LOGGER.info("Fetching prices for Store : " + storeID + " articleID " + articleID);
		Map<String, Object> prices = priceService.getPrices(storeID, articleID, page, pageSize);
        if (prices == null || prices.isEmpty()) {
            throw new PriceNotFoundException("No prices were found for the given request");
        }

		return prices;
	}
	
	@GetMapping("/getAllPrices")
    public List<Price> getPrices() {
		LOGGER.info("Fetching all prices");
        return priceService.findAll();
    }

}
