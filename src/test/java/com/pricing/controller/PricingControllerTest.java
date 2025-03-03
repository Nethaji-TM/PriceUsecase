package com.pricing.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.usecase.controller.PricingController;
import com.usecase.handler.PriceNotFoundException;
import com.usecase.main.PricingApplication;
import com.usecase.model.Price;
import com.usecase.service.PriceService;

@SpringBootTest(classes = PricingApplication.class)
@ExtendWith(MockitoExtension.class)
public class PricingControllerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingControllerTest.class);
	@Mock
    private PriceService priceService;

    @InjectMocks
    private PricingController pricingController;

    private MockMvc mockMvc;
    private String storeID = "7001";
    private String articleID = "1000102674";
    private Price price1;

    @BeforeEach
    void setUp() {
    	LOGGER.info("setUp() ENTRY");
        mockMvc = MockMvcBuilders.standaloneSetup(pricingController).build();
        price1 = new Price(storeID, articleID, "retail", "regular", "CAD", 30.0,
                Instant.parse("2023-12-31T23:59:59Z"), Instant.parse("9999-12-31T23:59:59Z"));
        LOGGER.info("setUp() EXIT");
    }

    @Test
    void testGetPrices_Success() throws Exception {
    	LOGGER.info("testGetPrices_Success() ENTRY");
        when(priceService.getPrices(storeID, articleID, 1, 2))
                .thenReturn(Map.of("article", articleID, "store", storeID, "prices", List.of(price1)));

        ResultActions actions = mockMvc.perform(get("/api/pricing/v1/prices/{storeID}/{articleID}", storeID, articleID)
                .param("page", "1")
                .param("pageSize", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article").value(articleID))
                .andExpect(jsonPath("$.store").value(storeID))
                .andExpect(jsonPath("$.prices").isArray());
        MvcResult result = actions.andReturn(); // Get full response
        String jsonResponse = result.getResponse().getContentAsString();
        LOGGER.info("testGetPrices_Success() EXIT : Test resp " + jsonResponse);
    }

    //@Test
    void testGetPrices_NotFound() throws Exception {
    	LOGGER.info("testGetPrices_NotFound() ENTRY");
        when(priceService.getPrices(storeID, articleID, 1, 2)).thenReturn(null);
        
        doThrow(new PriceNotFoundException("No prices were found for the given request"))
        .when(priceService).getPrices(storeID, articleID, 1, 2);

        ResultActions actions = mockMvc.perform(get("/api/pricing/v1/prices/{storeID}/{articleID}", storeID, articleID)
                .param("page", "1")
                .param("pageSize", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Verify 404 response
                .andExpect(jsonPath("$.error").value("No prices were found for the given request"));
        MvcResult result = actions.andReturn(); // Get full response
        String jsonResponse = result.getResponse().getContentAsString();
        
        LOGGER.info("testGetPrices_NotFound() EXIT Resp : " + jsonResponse);
    }

    @Test
    void testGetAllPrices() throws Exception {
    	LOGGER.info("testGetAllPrices() ENTRY");
        when(priceService.findAll()).thenReturn(List.of(price1));

        mockMvc.perform(get("/api/pricing/v1/getAllPrices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        LOGGER.info("testGetAllPrices() EXIT");
    }
}
