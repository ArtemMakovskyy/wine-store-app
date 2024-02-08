package com.winestoreapp.repository;

import com.winestoreapp.model.PurchaseObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseObjectRepository extends JpaRepository<PurchaseObject, Long> {

}
