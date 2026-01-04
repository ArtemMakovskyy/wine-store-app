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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private static final String REGULAR_EXPRESSION_SPACES = "\\s+";
    private static final String SPACE = " ";
    private static final int WORD_QUANTITY = 2;

    @Value("${telegram.bot.enabled}")
    private boolean telegramBotEnable;

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
        String[] nameParts = validateAndParseName(dto.getUserFirstAndLastName());
        validateWinesExist(dto.getCreateShoppingCardDto());

        User user = findOrUpdateOrSaveUser(nameParts[USER_FIRST_NAME_INDEX],
                nameParts[USER_LAST_NAME_INDEX],
                dto.getPhoneNumber(), dto.getEmail());

        // 1. Створюємо та ініціалізуємо замовлення (Domain Method)
        Order order = new Order();
        order.initializeNewOrder(user);
        order = orderRepository.save(order);

        // 2. Генерируємо номер (Domain Method)
        order.generateAndSetOrderNumber();

        // 3. Створюємо пов'язані сутності через приватні методи
        OrderDeliveryInformation delivery = createOrderDeliveryInformation(dto.getCreateOrderDeliveryInformationDto(), order);
        ShoppingCard card = createShoppingCard(dto.getCreateShoppingCardDto(), order);

        order.setDeliveryInformation(delivery);
        order.setShoppingCard(card);

        // Зберігаємо фінальний стан
        orderRepository.save(order);

        sendNotification(order, " is created.");
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public boolean updateOrderPaymentStatusAsPaidAndAddCurrentData(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by id: " + orderId));

        // Використовуємо логіку з моделі (Domain Method)
        order.markAsPaid();
        orderRepository.save(order);

        sendNotification(order, " has been paid");
        return true;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find Order by id: " + id));

        orderRepository.deleteById(id);
        sendNotification(order, " has been deleted.");
        return true;
    }

    @Override
    public OrderDto getById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find Order by id: " + id));
    }

    @Override
    public List<OrderDto> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).stream().map(orderMapper::toDto).toList();
    }

    @Override
    public List<OrderDto> findAllByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) throw new EntityNotFoundException("User not found");
        return orderRepository.findAllByUserId(userId, pageable).map(orderMapper::toDto).toList();
    }

    // --- Приватні методи з повною логікою ---

    private ShoppingCard createShoppingCard(CreateShoppingCardDto dto, Order order) {
        ShoppingCard shoppingCard = new ShoppingCard();
        shoppingCard.setOrder(order); // Встановлюємо зв'язок з Order

        Set<PurchaseObject> purchaseObjects = createPurchaseObjects(dto.getPurchaseObjects(), shoppingCard);
        shoppingCard.setPurchaseObjects(purchaseObjects);

        // Використовуємо метод моделі для розрахунку суми
        shoppingCard.recalculateTotal();

        return shoppingCardRepository.save(shoppingCard);
    }

    private Set<PurchaseObject> createPurchaseObjects(Set<CreatePurchaseObjectDto> dtos, ShoppingCard card) {
        Set<PurchaseObject> objects = new HashSet<>();
        for (CreatePurchaseObjectDto dto : dtos) {
            Wine wine = wineRepository.findById(dto.getWineId())
                    .orElseThrow(() -> new EntityNotFoundException("Wine not found: " + dto.getWineId()));

            PurchaseObject po = new PurchaseObject();
            po.setWine(wine);
            po.setPrice(wine.getPrice());
            po.setQuantity(dto.getQuantity());
            po.setShoppingCard(card); // Встановлюємо зв'язок з Card

            objects.add(purchaseObjectRepository.save(po));
        }
        return objects;
    }

    private OrderDeliveryInformation createOrderDeliveryInformation(CreateOrderDeliveryInformationDto dto, Order order) {
        OrderDeliveryInformation info = orderDeliveryInformationMapper.toEntity(dto);
        info.linkToOrder(order);
        return orderDeliveryInformationRepository.save(info);
    }

    private String[] validateAndParseName(String fullName) {
        String[] parts = fullName.strip().replaceAll(REGULAR_EXPRESSION_SPACES, SPACE).split(SPACE);
        if (parts.length != WORD_QUANTITY) {
            throw new RegistrationException("You should enter your first and last name with a space between them");
        }
        return parts;
    }

    private void validateWinesExist(CreateShoppingCardDto cardDto) {
        for (CreatePurchaseObjectDto item : cardDto.getPurchaseObjects()) {
            if (!wineRepository.existsById(item.getWineId())) {
                throw new EntityNotFoundException("Can't find wine by id " + item.getWineId());
            }
        }
    }

    private void sendNotification(Order order, String actionMessage) {
        if (telegramBotEnable && notificationService != null) {
            notificationService.sendNotification(
                    "Your order: " + order.getOrderNumber() + actionMessage,
                    order.getUser().getTelegramChatId()
            );
        }
    }

    private User findOrUpdateOrSaveUser(String fName, String lName, String phone, String email) {
        return userRepository.findUserByEmail(email)
                .or(() -> userRepository.findFirstByFirstNameAndLastName(fName, lName))
                .map(user -> {
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(new User(email, fName, lName, phone)));
    }
}
