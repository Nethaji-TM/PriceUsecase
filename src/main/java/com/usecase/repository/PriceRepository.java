package com.usecase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usecase.model.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
	List<Price> findByStoreIDAndArticleID(String storeID, String articleID);

}
