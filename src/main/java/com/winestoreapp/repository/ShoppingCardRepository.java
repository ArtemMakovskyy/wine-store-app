package com.winestoreapp.repository;

import com.winestoreapp.model.ShoppingCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCardRepository extends JpaRepository<ShoppingCard, Long> {

}
