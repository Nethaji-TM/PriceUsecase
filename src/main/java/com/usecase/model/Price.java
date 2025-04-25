package com.usecase.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Price {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String storeID;
	private String articleID;
	private String type;
	private String subtype;
	private String currency;
	private Double amount;
	private Instant validFrom;
	private Instant validTo;
	private boolean overlapped = false;

	public Price(String storeID, String articleID, String type, String subtype, String currency, Double amount,
			Instant validFrom, Instant validTo) {
		this.storeID = storeID;
		this.articleID = articleID;
		this.type = type;
		this.subtype = subtype;
		this.currency = currency;
		this.amount = amount;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public Price() { }

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getArticleID() {
		return articleID;
	}

	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Instant getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Instant validFrom) {
		this.validFrom = validFrom;
	}

	public Instant getValidTo() {
		return validTo;
	}

	public void setValidTo(Instant validTo) {
		this.validTo = validTo;
	}

	public boolean isOverlapped() {
		return overlapped;
	}

	public void setOverlapped(boolean overlapped) {
		this.overlapped = overlapped;
	}

	@Override
	public String toString() {
		return "Price [id=" + id + ", storeID=" + storeID + ", articleID=" + articleID + ", type=" + type + ", subtype="
				+ subtype + ", currency=" + currency + ", amount=" + amount + ", validFrom=" + validFrom + ", validTo="
				+ validTo + ", overlapped=" + overlapped + "]";
	}

}
