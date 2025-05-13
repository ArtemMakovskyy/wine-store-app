package com.winestoreapp.repository;

import com.winestoreapp.model.OrderDeliveryInformation;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;

@Observed
public interface OrderDeliveryInformationRepository
        extends JpaRepository<OrderDeliveryInformation, Long> {
}
