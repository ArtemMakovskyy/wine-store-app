package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.OrderDeliveryInformationMapper;
import com.winestoreapp.dto.mapper.OrderMapper;
import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.model.Order;
import com.winestoreapp.model.OrderDeliveryInformation;
import com.winestoreapp.model.OrderPaymentStatus;
import com.winestoreapp.model.PurchaseObject;
import com.winestoreapp.model.ShoppingCard;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.OrderDeliveryInformationRepository;
import com.winestoreapp.repository.OrderRepository;
import com.winestoreapp.repository.PurchaseObjectRepository;
import com.winestoreapp.repository.ShoppingCardRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PurchaseObjectRepository purchaseObjectRepository;
    private final ShoppingCardRepository shoppingCardRepository;
    private final WineRepository wineRepository;
    private final UserRepository userRepository;
    private final OrderDeliveryInformationMapper orderDeliveryInformationMapper;
    private final OrderDeliveryInformationRepository orderDeliveryInformationRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderDto dto) {
        Order order = new Order();
        order.setUser(userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id: " + dto.getUserId())));
        order.setRegistrationTime(LocalDateTime.now());
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        order = orderRepository.save(order);
        order.setDeliveryInformation(createOrderDeliveryInformation(
                dto.getCreateOrderDeliveryInformationDto(), order));
        order.setShoppingCard(createShoppingCard(
                dto.getCreateShoppingCardDto(), order));
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto getById(Long id) {
        final Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Order by id: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public boolean deleteById(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        throw new EntityNotFoundException("Can't find Order by id: " + id);
    }

    @Override
    @Transactional
    public boolean updateOrderPaymentStatusAsPaidAndAddCurrentData(Long orderId) {
        if (orderRepository.findById(orderId).isPresent()) {
            orderRepository.updateOrderPaymentStatusAsPaidAndSetCurrentDate(orderId);
            return true;
        }
        throw new EntityNotFoundException("Can't find order by id: " + orderId);
    }

    @Override
    public List<OrderDto> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> findAllByUserId(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable)
                .map(orderMapper::toDto)
                .toList();
    }

    private ShoppingCard createShoppingCard(CreateShoppingCardDto dto, Order order) {
        ShoppingCard shoppingCard = new ShoppingCard();
        final Set<PurchaseObject> purchaseObjects
                = createPurchaseObjectDtos(dto.getPurchaseObjects());
        shoppingCard.setPurchaseObjects(purchaseObjects);
        final BigDecimal totalCost = purchaseObjects.stream()
                .filter(purchaseObject -> !purchaseObject.isDeleted())
                .map(purchaseObject -> BigDecimal.valueOf(purchaseObject.getQuantity())
                        .multiply(purchaseObject.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        shoppingCard.setTotalCost(totalCost);
        shoppingCard.setOrder(order);
        return shoppingCardRepository.save(shoppingCard);
    }

    private Set<PurchaseObject> createPurchaseObjectDtos(
            Set<CreatePurchaseObjectDto> createPurchaseObjectDtos) {
        Set<PurchaseObject> purchaseObjects = new HashSet<>();
        for (CreatePurchaseObjectDto purchaseObjectDto : createPurchaseObjectDtos) {
            PurchaseObject purchaseObject = new PurchaseObject();
            final Wine wine = wineRepository.findById(purchaseObjectDto.getWineId()).orElseThrow(
                    () -> new EntityNotFoundException("Can't find wine by id: "
                            + purchaseObjectDto.getWineId()));
            purchaseObject.setWine(wine);
            purchaseObject.setPrice(wine.getPrice());
            purchaseObject.setQuantity(purchaseObjectDto.getQuantity());
            purchaseObjects.add(purchaseObjectRepository.save(purchaseObject));
        }
        return purchaseObjects;
    }

    private OrderDeliveryInformation createOrderDeliveryInformation(
            CreateOrderDeliveryInformationDto dto, Order order) {
        final OrderDeliveryInformation orderDeliveryInformation
                = orderDeliveryInformationMapper.toEntity(dto);
        orderDeliveryInformation.setOrder(order);
        return orderDeliveryInformationRepository.save(orderDeliveryInformation);
    }
}
