package com.winestoreapp.service;

import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto createOrder(CreateOrderDto dto);

    OrderDto getById(Long id);

    boolean deleteById(Long id);

    boolean updateOrderPaymentStatusAsPaidAndAddCurrentData(Long orderId);

    List<OrderDto> findAll(Pageable pageable);

    List<OrderDto> findAllByUserId(Long userId, Pageable pageable);
}
