package com.winestoreapp.repository;

import com.winestoreapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Query("""
            UPDATE Order o 
            SET o.completedTime = CURRENT_TIMESTAMP, o.paymentStatus = 'PAID'
            WHERE o.id = :orderId""")
    void updateOrderPaymentStatusAsPaidAndSetCurrentDate(
            @Param("orderId") Long orderId);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
