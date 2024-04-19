package com.winestoreapp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.winestoreapp.dto.mapper.OrderDeliveryInformationMapper;
import com.winestoreapp.dto.mapper.OrderMapper;
import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.order.delivery.information.OrderDeliveryInformationDto;
import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import com.winestoreapp.dto.shopping.card.ShoppingCardDto;
import com.winestoreapp.model.Order;
import com.winestoreapp.model.OrderDeliveryInformation;
import com.winestoreapp.model.OrderPaymentStatus;
import com.winestoreapp.model.PurchaseObject;
import com.winestoreapp.model.ShoppingCard;
import com.winestoreapp.model.User;
import com.winestoreapp.model.Wine;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.repository.OrderDeliveryInformationRepository;
import com.winestoreapp.repository.OrderRepository;
import com.winestoreapp.repository.PurchaseObjectRepository;
import com.winestoreapp.repository.ShoppingCardRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private WineRepository wineRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseObjectRepository purchaseObjectRepository;

    @Mock
    private OrderDeliveryInformationMapper orderDeliveryInformationMapper;

    @Mock
    private OrderDeliveryInformationRepository orderDeliveryInformationRepository;

    @Mock
    private ShoppingCardRepository shoppingCardRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Add order by valid data and return OrderDto")
    void createOrder_ValidData_ShouldReturnOrderDto() {
        //given
        CreateOrderDto createOrderDto = getCreateOrderDto();
        Order order = getOrder(createOrderDto);
        OrderDto expectedOrderDto = getOrderDto(createOrderDto);
        User user = getUser(createOrderDto);
        OrderDeliveryInformation orderDeliveryInformation
                = new OrderDeliveryInformation();
        PurchaseObject purchaseObject = getPurchaseObject();
        ShoppingCard shoppingCard = new ShoppingCard();
        Long wineId = 1L;

        Mockito.when(wineRepository.findById(wineId)).thenReturn(Optional.of(getWine()));
        Mockito.when(orderMapper.toDto(Mockito.any())).thenReturn(expectedOrderDto);
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findFirstByFirstNameAndLastName(
                Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(orderDeliveryInformationMapper.toEntity(Mockito.any()))
                .thenReturn(orderDeliveryInformation);
        Mockito.when(orderDeliveryInformationRepository.save(Mockito.any()))
                .thenReturn(orderDeliveryInformation);
        Mockito.when(purchaseObjectRepository.save(Mockito.any())).thenReturn(purchaseObject);
        Mockito.when(shoppingCardRepository.save(Mockito.any())).thenReturn(shoppingCard);

        //when
        final OrderDto actual = orderService.createOrder(createOrderDto);

        //then
        assertEquals(expectedOrderDto.getOrderNumber(), actual.getOrderNumber());
        verify(wineRepository, times(2)).findById(wineId);
        verify(orderMapper, times(1)).toDto(Mockito.any());
        verify(orderRepository, times(1)).save(Mockito.any());
        verify(userRepository, times(1)).findUserByEmail(Mockito.anyString());
        verify(userRepository, times(1))
                .findFirstByFirstNameAndLastName(Mockito.anyString(), Mockito.anyString());
        verify(userRepository, times(1)).save(Mockito.any());
        verify(orderDeliveryInformationMapper, times(1)).toEntity(Mockito.any());
        verify(orderDeliveryInformationRepository, times(1)).save(Mockito.any());
        verify(purchaseObjectRepository, times(1)).save(Mockito.any());
        verify(shoppingCardRepository, times(1)).save(Mockito.any());
        verifyNoMoreInteractions(wineRepository, orderMapper, orderRepository,
                userRepository, orderDeliveryInformationMapper,
                orderDeliveryInformationRepository, purchaseObjectRepository,
                shoppingCardRepository);
    }

    @Test
    @DisplayName("Get order by valid ID and return OrderDto")
    void getById_ValidId_ShouldReturnOrderDto() {
        //given
        final Order order = getOrder(getCreateOrderDto());
        Long orderId = Mockito.anyLong();
        OrderDto expectedOrderDto = getOrderDto(getCreateOrderDto());

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toDto(order)).thenReturn(expectedOrderDto);

        //when
        final OrderDto actual = orderService.getById(orderId);

        //then
        assertEquals(expectedOrderDto, actual);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toDto(order);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Delete order by valid ID and return true")
    void deleteById_ValidId_ShouldReturnTrue() {
        //given
        Long orderId = Mockito.anyLong();
        final Order order = getOrder(getCreateOrderDto());

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //when
        final boolean actual = orderService.deleteById(orderId);

        //then
        assertTrue(actual);
        verify(orderRepository, times(1)).deleteById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Update order payment status by valid ID and return true")
    void updateOrderPaymentStatusAsPaidAndAddCurrentData_ValidOrderId_ShouldReturnTrue() {
        //given
        Long orderId = Mockito.anyLong();
        final Order order = getOrder(getCreateOrderDto());

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //when
        final boolean actual
                = orderService.updateOrderPaymentStatusAsPaidAndAddCurrentData(orderId);

        //then
        assertTrue(actual);
        verify(orderRepository, times(1))
                .updateOrderPaymentStatusAsPaidAndSetCurrentDate(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Verify findAll method works")
    void findAll_ValidPageable_ShouldReturnAllOrders() {
        //given
        final Pageable pageable = PageRequest.of(0, 10);
        final List<Order> orders = List.of(getOrder(getCreateOrderDto()));
        final PageImpl<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        final OrderDto orderDto = getOrderDto(getCreateOrderDto());

        Mockito.when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        Mockito.when(orderMapper.toDto(Mockito.any())).thenReturn(orderDto);

        //when
        final List<OrderDto> actual = orderService.findAll(pageable);

        //then
        Assertions.assertEquals(orders.size(), 1);
        Assertions.assertTrue(actual.contains(orderDto));
        verify(orderRepository, times(1)).findAll(pageable);
        verify(orderMapper, times(1)).toDto(Mockito.any());
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Verify findAllByUserId method works")
    void findAllByUserId_ValidData_findAll_ValidPageable_ShouldReturnAllOrders() {
        //given
        final Pageable pageable = PageRequest.of(0, 10);
        final List<Order> orders = List.of(getOrder(getCreateOrderDto()));
        final PageImpl<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        final OrderDto orderDto = getOrderDto(getCreateOrderDto());
        Long userId = Mockito.anyLong();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(orderRepository.findAllByUserId(userId, pageable)).thenReturn(orderPage);
        Mockito.when(orderMapper.toDto(Mockito.any())).thenReturn(orderDto);

        //when
        final List<OrderDto> actual = orderService.findAllByUserId(userId, pageable);

        //then
        Assertions.assertEquals(orders.size(), 1);
        Assertions.assertTrue(actual.contains(orderDto));
        verify(userRepository, times(1)).existsById(userId);
        verify(orderRepository, times(1)).findAllByUserId(userId, pageable);
        verify(orderMapper, times(1)).toDto(Mockito.any());
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    private PurchaseObject getPurchaseObject() {
        PurchaseObject purchaseObject = new PurchaseObject();
        purchaseObject.setId(1L);
        purchaseObject.setWine(getWine());

        ShoppingCard shoppingCard = new ShoppingCard();
        purchaseObject.setShoppingCard(shoppingCard);
        purchaseObject.setQuantity(1);
        purchaseObject.setPrice(getWine().getPrice());
        purchaseObject.setDeleted(false);
        return purchaseObject;
    }

    private User getUser(CreateOrderDto dto) {
        User user = new User();
        user.setId(1L);
        user.setEmail(dto.getEmail());
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDeleted(false);
        return user;
    }

    private Order getOrder(CreateOrderDto dto) {
        Order order = new Order();
        order.setId(1L);
        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(dto.getEmail());
        order.setUser(user);
        order.setRegistrationTime(LocalDateTime.now());
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        return order;
    }

    private CreateOrderDto getCreateOrderDto() {
        CreateOrderDto orderDto = new CreateOrderDto();
        orderDto.setUserFirstAndLastName("Ivan Ivanov");
        orderDto.setEmail("ivanov.ivanov.123456@gmail.com");
        orderDto.setPhoneNumber("+380501234567");

        CreateShoppingCardDto createShoppingCardDto = new CreateShoppingCardDto();
        CreatePurchaseObjectDto createPurchaseObjectDto = new CreatePurchaseObjectDto();
        createPurchaseObjectDto.setQuantity(1);
        createPurchaseObjectDto.setWineId(1L);
        createShoppingCardDto.setPurchaseObjects(Set.of(createPurchaseObjectDto));
        orderDto.setCreateShoppingCardDto(createShoppingCardDto);

        CreateOrderDeliveryInformationDto createOrderDeliveryInformationDto
                = new CreateOrderDeliveryInformationDto();
        createOrderDeliveryInformationDto.setZipCode("12345");
        createOrderDeliveryInformationDto.setRegion("Kyiv region");
        createOrderDeliveryInformationDto.setCity("Kyiv");
        createOrderDeliveryInformationDto.setStreet("Lobanovskogo str, 13/1, ap. 16");
        createOrderDeliveryInformationDto.setComment("Some info");
        orderDto.setCreateOrderDeliveryInformationDto(createOrderDeliveryInformationDto);
        return orderDto;
    }

    private Wine getWine() {
        Wine wine = new Wine();
        wine.setId(1L);
        wine.setVendorCode("MRD2019");
        wine.setQualityLevel("Select");
        wine.setReserveType(null);
        wine.setName("Prince Trubetskoi Select Riesling");
        wine.setShortName("Riesling");
        wine.setYear(2019);
        wine.setTasteWine("asian food");
        wine.setPrice(new BigDecimal("870"));
        wine.setGrape("Riesling");
        wine.setWineType(WineType.DRY);
        wine.setWineColor(WineColor.WHITE);
        return wine;
    }

    private OrderDto getOrderDto(CreateOrderDto dto) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setOrderNumber("Order_ABC123");
        orderDto.setUserId(getUser(dto).getId());
        orderDto.setShoppingCardDto(new ShoppingCardDto());
        orderDto.setOrderDeliveryInformationDto(new OrderDeliveryInformationDto());
        orderDto.setPaymentStatus(OrderPaymentStatus.PAID);
        return orderDto;
    }
}
