package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.OrderDeliveryInformationMapper;
import com.winestoreapp.dto.mapper.OrderMapper;
import com.winestoreapp.dto.order.CreateOrderDto;
import com.winestoreapp.dto.order.OrderDto;
import com.winestoreapp.dto.order.delivery.information.CreateOrderDeliveryInformationDto;
import com.winestoreapp.dto.purchase.object.CreatePurchaseObjectDto;
import com.winestoreapp.dto.shopping.card.CreateShoppingCardDto;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.model.Order;
import com.winestoreapp.model.OrderDeliveryInformation;
import com.winestoreapp.model.OrderPaymentStatus;
import com.winestoreapp.model.PurchaseObject;
import com.winestoreapp.model.ShoppingCard;
import com.winestoreapp.model.User;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.OrderDeliveryInformationRepository;
import com.winestoreapp.repository.OrderRepository;
import com.winestoreapp.repository.PurchaseObjectRepository;
import com.winestoreapp.repository.ShoppingCardRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.NotificationService;
import com.winestoreapp.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private static final String ORDER_IDENTIFIER = "ORDER_";
    private static final String REGULAR_EXPRESSION_SPACES = "\\s+";
    private static final String SPACE = " ";
    private static final int NUMBER_RANDOM_CHARACTERS = 3;
    private static final int NUMBER_OF_LETTERS_IN_THE_ENGLISH_ALPHABET = 26;
    private static final int WORD_QUANTITY = 2;
    @Value("${telegram.bot.enabled}")
    private boolean telegramBotEnable;
    @Autowired(required = false)
    @Nullable
    private final NotificationService notificationService;
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
        final String[] userFirstAndLastName
                = dto.getUserFirstAndLastName()
                .strip()
                .replaceAll(REGULAR_EXPRESSION_SPACES, SPACE)
                .split(SPACE);
        if (userFirstAndLastName.length != WORD_QUANTITY) {
            throw new RegistrationException(
                    "You should enter your first and last name with a space between them");
        }
        for (CreatePurchaseObjectDto wine : dto.getCreateShoppingCardDto().getPurchaseObjects()) {
            if (wineRepository.findById(wine.getWineId()).isEmpty()) {
                throw new EntityNotFoundException("Can't find wine by id " + wine.getWineId());
            }
        }
        Order order = new Order();
        order.setUser(findOrUpdateOrSaveUser(
                userFirstAndLastName[USER_FIRST_NAME_INDEX],
                userFirstAndLastName[USER_LAST_NAME_INDEX],
                dto.getPhoneNumber(),
                dto.getEmail()));
        order.setRegistrationTime(LocalDateTime.now());
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        order = orderRepository.save(order);
        order.setOrderNumber(generateRandomLetters(order.getId()));
        order.setDeliveryInformation(createOrderDeliveryInformation(
                dto.getCreateOrderDeliveryInformationDto(), order));
        order.setShoppingCard(createShoppingCard(
                dto.getCreateShoppingCardDto(), order));
        if (telegramBotEnable) {
            notificationService.sendNotification(
                    "Your order: " + order.getOrderNumber()
                            + " is created.", order.getUser().getTelegramChatId());
        }
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
        final Optional<Order> optionalOrderById = orderRepository.findById(id);
        if (optionalOrderById.isPresent()) {
            orderRepository.deleteById(id);
            final Order order = optionalOrderById.orElseThrow(
                    () -> new EntityNotFoundException("Can't find order by id: " + id));
            if (telegramBotEnable) {
                notificationService.sendNotification(
                        "Your order: " + order.getOrderNumber() + " has been deleted.",
                        order.getUser().getTelegramChatId());
            }
            return true;
        }
        throw new EntityNotFoundException("Can't find Order by id: " + id);
    }

    @Override
    @Transactional
    public boolean updateOrderPaymentStatusAsPaidAndAddCurrentData(Long orderId) {
        final Optional<Order> optionalOrderById = orderRepository.findById(orderId);
        if (optionalOrderById.isPresent()) {
            orderRepository.updateOrderPaymentStatusAsPaidAndSetCurrentDate(orderId);
            final Order order = optionalOrderById.orElseThrow(
                    () -> new EntityNotFoundException("Can't find order by id: " + orderId));
            if (telegramBotEnable) {
                notificationService.sendNotification(
                        "Your order: " + order.getOrderNumber() + " has been paid",
                        order.getUser().getTelegramChatId());
            }
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
        if (userRepository.existsById(userId)) {
            return orderRepository.findAllByUserId(userId, pageable)
                    .map(orderMapper::toDto)
                    .toList();
        }
        throw new EntityNotFoundException("Can't find user by id " + userId);
    }

    private String generateRandomLetters(Long orderId) {
        Random random = new Random();
        StringBuilder result = new StringBuilder(ORDER_IDENTIFIER);
        for (int i = 0; i < NUMBER_RANDOM_CHARACTERS; i++) {
            char randomLetter = (char)
                    ('A' + random.nextInt(NUMBER_OF_LETTERS_IN_THE_ENGLISH_ALPHABET));
            result.append(randomLetter);
        }
        return result.append(orderId).toString();
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

    private User findOrUpdateOrSaveUser(
            String userFirstName,
            String userLastName,
            String phoneNumber,
            String email) {

        final Optional<User> findUserByEmail = userRepository.findUserByEmail(email);
        if (findUserByEmail.isPresent()) {
            return findUserByEmail.get();
        }

        final Optional<User> findUserByFirstNameAndLastName
                = userRepository.findFirstByFirstNameAndLastName(userFirstName, userLastName);
        if (findUserByFirstNameAndLastName.isPresent()) {
            final User user = findUserByFirstNameAndLastName.get();
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            return userRepository.save(user);
        }
        return userRepository.save(new User(email, userFirstName, userLastName, phoneNumber));
    }
}
