package com.winestoreapp.controller;

import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints to managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Find all orders",
            description = "Find all orders. Use size, page and sort for pagination")
    @GetMapping
    public List<OrderDto> findAllOrders(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @Operation(summary = "Find all orders by user ID",
            description = "Find all orders by user ID. Use size, page and sort for pagination")
    @GetMapping("/users/{userId}")
    public List<OrderDto> findAllOrdersByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        return orderService.findAllByUserId(userId, pageable);
    }

    @Operation(summary = "Add new order",
            description = "Save new order into database")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderDto addOrder(@RequestBody @Valid CreateOrderDto dto) {
        return orderService.createOrder(dto);
    }

    @Operation(summary = "Set the PAID status",
            description = """
                    Set the status PAID for the order and set current data. 
                    Available for manager12345@gmail.com""")
    @ResponseStatus(HttpStatus.OK)
    //    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/paid")
    public boolean setPaidStatus(@PathVariable Long id) {
        return orderService.updateOrderPaymentStatusAsPaidAndAddCurrentData(id);
    }

    @Operation(summary = "Find order by id",
            description = "Find order by id from database")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @Operation(summary = "Delete order by id",
            description = "Delete order by id from database")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public boolean deleteOrderById(@PathVariable Long id) {
        return orderService.deleteById(id);
    }
}