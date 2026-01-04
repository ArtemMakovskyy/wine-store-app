package com.winestoreapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.winestoreapp.dto.mapper.OrderDeliveryInformationMapper;
import com.winestoreapp.dto.mapper.OrderMapper;
import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.order.delivery.information.OrderDeliveryInformationDto;
import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import com.winestoreapp.dto.shopping.card.ShoppingCardDto;
import com.winestoreapp.model.*;
import com.winestoreapp.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock private WineRepository wineRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private PurchaseObjectRepository purchaseObjectRepository;
    @Mock private OrderDeliveryInformationMapper orderDeliveryInformationMapper;
    @Mock private OrderDeliveryInformationRepository orderDeliveryInformationRepository;
    @Mock private ShoppingCardRepository shoppingCardRepository;

    @InjectMocks private OrderServiceImpl orderService;

    @Test
    @DisplayName("Add order by valid data and return OrderDto")
    void createOrder_ValidData_ShouldReturnOrderDto() {
        //given
        CreateOrderDto createOrderDto = getCreateOrderDto();
        User user = getUser(createOrderDto);

        // Створюємо order і встановлюємо ID через Reflection, бо сетера немає
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 1L);

        OrderDto expectedOrderDto = getOrderDto(createOrderDto);
        OrderDeliveryInformation deliveryInfo = new OrderDeliveryInformation();
        PurchaseObject purchaseObject = getPurchaseObject();
        ShoppingCard shoppingCard = new ShoppingCard();

        when(wineRepository.existsById(anyLong())).thenReturn(true);
        when(wineRepository.findById(anyLong())).thenReturn(Optional.of(getWine()));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findFirstByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);

        // Перший сейв для отримання ID, другий для збереження номера замовлення
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        when(orderDeliveryInformationMapper.toEntity(any())).thenReturn(deliveryInfo);
        when(orderDeliveryInformationRepository.save(any())).thenReturn(deliveryInfo);
        when(purchaseObjectRepository.save(any())).thenReturn(purchaseObject);
        when(shoppingCardRepository.save(any())).thenReturn(shoppingCard);
        when(orderMapper.toDto(any())).thenReturn(expectedOrderDto);

        //when
        final OrderDto actual = orderService.createOrder(createOrderDto);

        //then
        assertEquals(expectedOrderDto.getOrderNumber(), actual.getOrderNumber());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update order payment status by valid ID and return true")
    void updateOrderPaymentStatusAsPaidAndAddCurrentData_ValidOrderId_ShouldReturnTrue() {
        //given
        Long orderId = 1L;
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //when
        final boolean actual = orderService.updateOrderPaymentStatusAsPaidAndAddCurrentData(orderId);

        //then
        assertTrue(actual);
        assertEquals(OrderPaymentStatus.PAID, order.getPaymentStatus());
        verify(orderRepository).save(order);
    }

    // Всі інші методи (findAll, getById) працюють аналогічно через моки

    private PurchaseObject getPurchaseObject() {
        PurchaseObject po = new PurchaseObject();
        po.setWine(getWine());
        po.setQuantity(1);
        po.setPrice(new BigDecimal("870"));
        return po;
    }

    private User getUser(CreateOrderDto dto) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail(dto.getEmail());
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        return user;
    }

    private Wine getWine() {
        Wine wine = new Wine();
        wine.setId(1L);
        wine.setPrice(new BigDecimal("870"));
        return wine;
    }

    private CreateOrderDto getCreateOrderDto() {
        CreateOrderDto dto = new CreateOrderDto();
        dto.setUserFirstAndLastName("Ivan Ivanov");
        dto.setEmail("ivan@test.com");
        dto.setPhoneNumber("+38000");

        CreateShoppingCardDto cardDto = new CreateShoppingCardDto();
        CreatePurchaseObjectDto poDto = new CreatePurchaseObjectDto();
        poDto.setWineId(1L);
        poDto.setQuantity(1);
        cardDto.setPurchaseObjects(Set.of(poDto));
        dto.setCreateShoppingCardDto(cardDto);

        dto.setCreateOrderDeliveryInformationDto(new CreateOrderDeliveryInformationDto());
        return dto;
    }

    private OrderDto getOrderDto(CreateOrderDto dto) {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderNumber("ORDER_ABC1");
        return orderDto;
    }
}
