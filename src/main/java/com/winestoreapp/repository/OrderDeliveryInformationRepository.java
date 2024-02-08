package com.winestoreapp.repository;

import com.winestoreapp.model.OrderDeliveryInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDeliveryInformationRepository
        extends JpaRepository<OrderDeliveryInformation, Long> {

}
