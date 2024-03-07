package com.winestoreapp.repository;

import com.winestoreapp.model.PurchaseObject;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseObjectRepository extends JpaRepository<PurchaseObject, Long> {
    @EntityGraph(attributePaths = {"wine"})
    Optional<PurchaseObject> findById(Long id);
}
