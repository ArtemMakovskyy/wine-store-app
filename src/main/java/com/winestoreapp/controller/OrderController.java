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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints to managing orders")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PATCH, RequestMethod.DELETE})
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Find all orders",
            description = """
                    Find all orders. Use size, page and sort for pagination.
                    Pagination example: /orders?size=5&page=0&sort=id
                    Available for all users""")
    @GetMapping
    public List<OrderDto> findAllOrders(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @Operation(summary = "Find all orders by user ID",
            description = """
                    Find all orders by user ID. Use size, page and sort for pagination.
                    Pagination example: /orders/users/{userId}?size=5&page=0&sort=id
                    Available for all users""")
    @GetMapping("/users/{userId}")
    public List<OrderDto> findAllOrdersByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        return orderService.findAllByUserId(userId, pageable);
    }

    @Operation(summary = "Add new order",
            description = """
                    Adds an order for wine from a specific User. Users are identified by first 
                    name, last name, and telephone number. If there is already a user in the 
                    database with first name, last name, and phone number, this user is linked 
                    to the order. Otherwise, the database is searched for a user by first and 
                    last name, and if one is found, a phone number is added to the found user 
                    and linked to the order. If in this case there is no user in the database, 
                    it is created anew. Available to all users.""")
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
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/paid")
    public boolean setPaidStatus(@PathVariable Long id) {
        return orderService.updateOrderPaymentStatusAsPaidAndAddCurrentData(id);
    }

    @Operation(summary = "Find order by id",
            description = "Find order by id from database. Available for all users")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @Operation(summary = "Delete order by id",
            description = "Delete order by id from database. Available for manager12345@gmail.com")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    public boolean deleteOrderById(@PathVariable Long id) {
        return orderService.deleteById(id);
    }
}
