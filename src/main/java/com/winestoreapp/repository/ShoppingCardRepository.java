package com.winestoreapp.repository;

import com.winestoreapp.model.ShoppingCard;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCardRepository extends JpaRepository<ShoppingCard, Long> {
    @EntityGraph(attributePaths = { "purchaseObjects.wine"})
    Optional<ShoppingCard> findById(Long id);
}
