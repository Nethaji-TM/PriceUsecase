package com.usecase.dto;

import java.time.Instant;

public record PriceRequest(
	Long id,
	String storeID,
	String articleID,
	String type,
	String subtype,
	String currency,
	Double amount,
	Instant validFrom,
	Instant validTo,
	boolean overlapped
) {}
