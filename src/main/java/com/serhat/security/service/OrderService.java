package com.serhat.security.service;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCardRepository shoppingCardRepository;
    private final TokenInterface tokenInterface;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeService discountCodeService;
    private final TransactionService transactionService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final DiscountCodeService discountService;
    private final NotificationService notificationService;
    private final StockAlertService stockAlertService;
    private boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
    }

    @Transactional
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = validateAndGetUser(request, orderRequest);
        paymentService.findWalletForUser(user);
        List<ShoppingCard> shoppingCards = getValidatedShoppingCart(user);

        PriceDetails priceDetails = paymentService.calculatePriceDetails(shoppingCards, user, orderRequest);
        updateUserBonusPoints(user, priceDetails.bonusPoints());

        Order order = orderMapper.createOrderEntity(user, orderRequest, priceDetails);
        processOrderItems(order, shoppingCards);

        finalizeOrder(order, user, shoppingCards, request);
        notificationService.addOrderNotification(user,order,NotificationTopic.ORDER_PLACED);

        return orderMapper.toOrderResponse(order);
    }

    private User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);
        if (!isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId())) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }
        return user;
    }

    private List<ShoppingCard> getValidatedShoppingCart(User user) {
        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);
        if (shoppingCards.isEmpty()) {
            throw new EmptyShoppingCardException("No products in the shopping cart!");
        }
        return shoppingCards;
    }

    private void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }

    private void processOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = createOrderItems(order, shoppingCards);
        List<Transaction> transactions = paymentService.createOrderTransactions(order);

        order.setOrderItems(orderItems);
        order.setTransactions(transactions);
    }

    private List<OrderItem> createOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> createOrderItem(order, sc))
                .collect(Collectors.toList());
    }

    public void manageStockAlerts(Long productId) {
        stockAlertService.handleStockAlert(productId);
    }

    private OrderItem createOrderItem(Order order, ShoppingCard sc) {
        Product product = sc.getProduct();
        validateProductStock(product, sc.getQuantity());
        updateProductStock(product, sc.getQuantity());
        manageStockAlerts(product.getProductId());

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(sc.getQuantity())
                .price(product.getPrice())
                .isReturnable(product.isReturnable())
                .build();
    }

    private void validateProductStock(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
    }

    private void updateProductStock(Product product, int quantity) {
        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() == 0) {
            product.setStockStatus(StockStatus.OUT_OF_STOCKS);
        }
        if(product.getQuantity()<0){
            throw new InvalidQuantityException("Quantity cannot be negative");
        }
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        saveOrderAndUpdateUser(order, user);
        clearShoppingCart(shoppingCards);
        saveUpdatedProducts(order.getOrderItems());
        updateDiscountCodeStatus(order.getDiscountCode());
        generateDiscountCodeIfEligible(order, request);
        updateUserTotalFees(user);

        BigDecimal totalSaved = order.getBonusPointsUsed().add(order.getTotalDiscount());
        user.setTotalSaved(user.getTotalSaved().add(totalSaved));
    }

    private void saveOrderAndUpdateUser(Order order, User user) {
        orderRepository.save(order);
        user.setTotalOrders(user.getTotalOrders() + 1);
    }

    private void clearShoppingCart(List<ShoppingCard> shoppingCards) {
        shoppingCardRepository.deleteAll(shoppingCards);
    }

    private void saveUpdatedProducts(List<OrderItem> orderItems) {
        productRepository.saveAll(orderItems.stream()
                .map(OrderItem::getProduct)
                .collect(Collectors.toList()));
    }

    private void updateDiscountCodeStatus(DiscountCode discountCode) {
        if (discountCode != null) {
            discountCode.setStatus(CouponStatus.USED);
        }
    }

    private void updateUserTotalFees(User user) {
        BigDecimal totalShippingFee = orderRepository.findByUser(user).stream()
                .map(Order::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrderFee = orderRepository.findByUser(user).stream()
                .map(order -> order.getTotalPaid().subtract(order.getShippingFee()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        user.setTotalShippingFeePaid(totalShippingFee);
        user.setTotalOrderFeePaid(totalOrderFee);
        userRepository.save(user);
    }

    private void generateDiscountCodeIfEligible(Order order, HttpServletRequest request) {
        if (order.getTotalPrice().compareTo(discountService.getDiscountThreshold()) >= 0) {
            discountCodeService.generateDiscountCode(request);
        }
    }

    public Page<OrderResponse> getOrdersByUser(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Page<Order> orders = orderRepository.findByUser(user, pageable);

        if (orders.isEmpty()) {
            throw new NoOrderException("No orders found");
        }

        return orders.map(orderMapper::toOrderResponse);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            long minutesSinceOrder = Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();
           if (order.getStatus() == OrderStatus.APPROVED && minutesSinceOrder >= 60) {
                order.setStatus(OrderStatus.SHIPPED);
                notificationService.addOrderNotification(order.getUser(), order, NotificationTopic.ORDER_SHIPPED);
                log.info("Order {} status updated to SHIPPED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.SHIPPED && minutesSinceOrder >= 180) {
                order.setStatus(OrderStatus.DELIVERED);
                notificationService.addOrderNotification(order.getUser(), order, NotificationTopic.ORDER_DELIVERED);
                log.info("Order {} status updated to DELIVERED", order.getOrderId());
            }
        }
        orderRepository.saveAll(orders);
    }

    @Transactional
    public OrderCancellationResponse cancelOrder(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new WrongOrderIdException("Wrong Order Id!");
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Order cannot be canceled as it is already shipped or delivered!");
        }

        boolean isItemsReturnable = order.getOrderItems().stream()
                .allMatch(orderItem -> orderItem.getProduct().isReturnable());

        if (!isItemsReturnable) {
            throw new OrderCancellationException("All the items must be returnable to cancel the order!");
        }

        Wallet wallet = paymentService.findWalletForUser(user);
        BigDecimal shippingFee = paymentService.calculateShippingFee(user, order.getTotalPrice());
        BigDecimal totalPaid = order.getTotalPaid();

        if (order.getBonusPointsUsed().compareTo(BigDecimal.ZERO) > 0) {
            user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(order.getBonusPointsUsed()));
            wallet.setBonusPoints(user.getCurrentBonusPoints());
            user.setTotalSaved(user.getTotalSaved().subtract(order.getBonusPointsUsed()));
        }

        if (order.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0) {
            user.setTotalSaved(user.getTotalSaved().subtract(order.getTotalDiscount()));
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());

            if (product.getQuantity() > 0 && product.getStockStatus() == StockStatus.OUT_OF_STOCKS) {
                product.setStockStatus(StockStatus.AVAILABLE);
            }
        }

        user.setCancelledOrders(user.getCancelledOrders() + 1);
        user.setTotalShippingFeePaid(user.getTotalShippingFeePaid().subtract(shippingFee));
        user.setTotalOrderFeePaid(user.getTotalOrderFeePaid().subtract(order.getTotalPaid()));

        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getPaymentMethod().equals(PaymentMethod.E_WALLET)) {
            transactionService.createRefundTransaction(order, user, totalPaid, shippingFee);
        }

        orderRepository.save(order);
        productRepository.saveAll(order.getOrderItems().stream().map(OrderItem::getProduct).toList());
        userRepository.save(user);
        notificationService.addOrderNotification(user, order, NotificationTopic.ORDER_CANCELLED);

        return orderMapper.toOrderCancellationResponse(order, totalPaid);
    }
    public OrderResponse getOrderDetails(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return orderMapper.toOrderResponse(order);
    }
}
