package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final WalletRepository walletRepository;
    private final DiscountCodeService discountCodeService;
    private final DiscountCodeRepository discountCodeRepository;
    private final TransactionService transactionService;
    private boolean isAddressBelongsToUser(Long addressId, Long userId) {
        return addressRepository.existsByAddressIdAndUserUserId(addressId, userId);
    }

    private AddressDto convertToAddressDto(Long addressId) {
        return addressRepository.findById(addressId)
                .map(address -> new AddressDto(
                        address.getAddressId(), address.getCountry(), address.getCity(),
                        address.getStreet(), address.getAptNo(), address.getFlatNo(),
                        address.getDescription(), address.getAddressType()))
                .orElseThrow(() -> new AddressNotFoundException("Address not found for ID: " + addressId));
    }

    private List<OrderItemDetails> convertToOrderItemDetails(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> OrderItemDetails.builder()
                        .productCode(item.getProduct().getProductCode())
                        .productName(item.getProduct().getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .brand(item.getProduct().getBrand())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal calculateShippingFee(User user, BigDecimal totalPrice) {
        switch (user.getMembershipPlan()) {
            case VIP -> {
                return BigDecimal.ZERO;
            }
            case PREMIUM -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(100)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(6.99);
            }
            case BASIC -> {
                return (totalPrice.compareTo(BigDecimal.valueOf(200)) >= 0) ? BigDecimal.ZERO : BigDecimal.valueOf(10.99);
            }
        }
        return BigDecimal.ZERO;
    }


    private BigDecimal calculateBonusPoints(User user, BigDecimal totalPrice) {
        BigDecimal bonusRate = switch (user.getMembershipPlan()) {
            case VIP -> BigDecimal.valueOf(0.05);
            case PREMIUM -> BigDecimal.valueOf(0.03);
            case BASIC -> BigDecimal.valueOf(0.01);
        };
        return totalPrice.multiply(bonusRate);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        AddressDto shippingAddress = convertToAddressDto(order.getShippingAddressId());
        List<OrderItemDetails> orderItems = convertToOrderItemDetails(order.getOrderItems());

        BigDecimal cartTotalPrice = order.getTotalPrice();
        BigDecimal totalBeforeDiscount = cartTotalPrice.add(order.getShippingFee());

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderDate(),
                order.getStatus(),
                shippingAddress,
                order.getPaymentMethod(),
                order.getTotalPrice(),
                order.getShippingFee(),
                totalBeforeDiscount,
                order.getTotalDiscount(),
                order.getBonusPointsUsed(),
                order.getTotalPaid(),
                order.getNotes(),
                orderItems,
                order.getBonusWon()
        );
    }


    @Transactional
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = validateAndGetUser(request, orderRequest);
        findWalletForUser(user);
        List<ShoppingCard> shoppingCards = getValidatedShoppingCart(user);

        PriceDetails priceDetails = calculatePriceDetails(shoppingCards, user, orderRequest);
        updateUserBonusPoints(user, priceDetails.bonusPoints());

        Order order = createOrderEntity(user, orderRequest, priceDetails);
        processOrderItems(order, shoppingCards);

        finalizeOrder(order, user, shoppingCards, request);

        return convertToOrderResponse(order);
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
    private PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards, User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = calculateTotalPrice(shoppingCards);
        BigDecimal originalTotalPrice = totalPrice;
        BigDecimal shippingFee = calculateShippingFee(user, totalPrice);
        BigDecimal bonusPoints = calculateBonusPoints(user, totalPrice);

        DiscountDetails discountDetails = applyDiscountIfAvailable(orderRequest, originalTotalPrice, user);
        totalPrice = totalPrice.subtract(discountDetails.discountAmount());

        BonusUsageResult bonusUsageResult = useBonusIfRequested(user, orderRequest, totalPrice);
        totalPrice = bonusUsageResult.updatedTotalPrice();
        BigDecimal bonusPointsUsed = bonusUsageResult.bonusPointsUsed();

        BigDecimal finalPrice = totalPrice.add(shippingFee);

        return new PriceDetails(totalPrice, originalTotalPrice, shippingFee, bonusPoints,
                discountDetails.discountAmount(), finalPrice, discountDetails.discountCode(), bonusPointsUsed);
    }

    private BigDecimal calculateTotalPrice(List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> sc.getProduct().getPrice().multiply(new BigDecimal(sc.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private DiscountDetails applyDiscountIfAvailable(OrderRequest orderRequest, BigDecimal originalTotalPrice, User user) {
        if (orderRequest.discountId() == null) {
            return new DiscountDetails(BigDecimal.ZERO, null);
        }

        DiscountCode discountCode = discountCodeRepository.findById(orderRequest.discountId())
                .orElseThrow(() -> new InvalidDiscountCodeException("Invalid discount code"));
        validateDiscountCode(discountCode, user);

        BigDecimal discountAmount = calculateDiscountAmount(originalTotalPrice, discountCode);
        return new DiscountDetails(discountAmount, discountCode);
    }

    private BigDecimal calculateDiscountAmount(BigDecimal originalTotalPrice, DiscountCode discountCode) {
        return originalTotalPrice
                .multiply(BigDecimal.valueOf(discountCode.getDiscountRate().getPercentage() / 100.0));
    }

    private void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));
        user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(bonusPoints));
    }

    private Order createOrderEntity(User user, OrderRequest orderRequest, PriceDetails priceDetails) {
        return Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(priceDetails.originalTotalPrice())
                .status(OrderStatus.APPROVED)
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(PaymentMethod.E_WALLET)
                .notes(orderRequest.notes())
                .updatedAt(LocalDateTime.now())
                .shippingFee(priceDetails.shippingFee())
                .bonusWon(priceDetails.bonusPoints())
                .discountCode(priceDetails.discountCode())
                .totalDiscount(priceDetails.discountAmount())
                .discountRate(priceDetails.discountCode() != null ?
                        priceDetails.discountCode().getDiscountRate() : DiscountRate.ZERO)
                .totalPaid(priceDetails.finalPrice())
                .isBonusPointUsed(priceDetails.bonusPointsUsed().compareTo(BigDecimal.ZERO) > 0)
                .bonusPointsUsed(priceDetails.bonusPointsUsed())
                .build();
    }

    private void processOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = createOrderItems(order, shoppingCards);
        List<Transaction> transactions = createOrderTransactions(order);

        order.setOrderItems(orderItems);
        order.setTransactions(transactions);
    }

    private List<OrderItem> createOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> createOrderItem(order, sc))
                .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(Order order, ShoppingCard sc) {
        Product product = sc.getProduct();
        validateProductStock(product, sc.getQuantity());
        updateProductStock(product, sc.getQuantity());

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(sc.getQuantity())
                .price(product.getPrice())
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
    }

    private List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        saveOrderAndUpdateUser(order, user);
        clearShoppingCart(shoppingCards);
        saveUpdatedProducts(order.getOrderItems());
        updateDiscountCodeStatus(order.getDiscountCode());
        generateDiscountCodeIfEligible(order, request);
        updateUserTotalFees(user);
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

    private void findWalletForUser(User user) {
         walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
    }



    private BonusUsageResult useBonusIfRequested(User user, OrderRequest orderRequest, BigDecimal totalPrice) {
        BigDecimal bonusPointsUsed = BigDecimal.ZERO;

        if (orderRequest.useBonus() != null && orderRequest.useBonus()) {
            BigDecimal availableBonusPoints = user.getCurrentBonusPoints();
            if (availableBonusPoints.compareTo(BigDecimal.ZERO) > 0) {
                bonusPointsUsed = availableBonusPoints.min(totalPrice);
                totalPrice = totalPrice.subtract(bonusPointsUsed);

                user.setCurrentBonusPoints(availableBonusPoints.subtract(bonusPointsUsed));
                user.setTotalSaved(user.getTotalSaved().add(bonusPointsUsed));
            }
        }

        return new BonusUsageResult(totalPrice, bonusPointsUsed);
    }



    private void validateDiscountCode(DiscountCode discountCode, User user) {
        if (discountCode.getUser() != null && !discountCode.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidDiscountCodeException("This discount code is not valid for the current user!");
        }

        if (discountCode.getStatus().equals(CouponStatus.EXPIRED)) {
            throw new DiscountCodeExpiredException("This coupon has expired!");
        }

        if (discountCode.getStatus().equals(CouponStatus.USED)) {
            throw new CouponAlreadyUsedException("The coupon you entered is already used");
        }
    }

    private void generateDiscountCodeIfEligible(Order order , HttpServletRequest request){
        if(order.getTotalPrice().compareTo(new BigDecimal("800.00")) >=0){
            discountCodeService.generateDiscountCode(request);
        }
    }


    public List<OrderResponse> getOrdersByUser(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<Order> orders = orderRepository.findByUser(user);

        if (orders.isEmpty()) {
            throw new NoOrderException("No orders found");
        }

        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            long minutesSinceOrder = java.time.Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();

            if (order.getStatus() == OrderStatus.PENDING && minutesSinceOrder >= 15) {
                order.setStatus(OrderStatus.APPROVED);
                log.info("Order {} status updated to APPROVED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.APPROVED && minutesSinceOrder >= 60) {
                order.setStatus(OrderStatus.SHIPPED);
                log.info("Order {} status updated to SHIPPED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.SHIPPED && minutesSinceOrder >= 180) {
                order.setStatus(OrderStatus.DELIVERED);
                log.info("Order {} status updated to DELIVERED", order.getOrderId());
            } else if (order.getStatus() == OrderStatus.CANCELLED) {
                long minutesSinceCancellation = java.time.Duration.between(order.getUpdatedAt(), LocalDateTime.now()).toMinutes();
                if (minutesSinceCancellation >= 15) {
                    order.setStatus(OrderStatus.REFUNDED);

                    if (order.getPaymentMethod().equals(PaymentMethod.E_WALLET)) {
                        transactionService.createRefundTransaction(order, order.getUser(), order.getTotalPrice(), order.getShippingFee());
                        log.info("Order {} REFUNDED: {} added back to wallet", order.getOrderId(), order.getTotalPrice());
                    }
                    log.info("Order {} status updated to REFUNDED", order.getOrderId());
                }
            }
        }
        orderRepository.saveAll(orders);
    }

    private void updateUserTotalFees(User user) {
        BigDecimal totalShippingFee = orderRepository.findByUser(user).stream()
                .map(Order::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrderFee = orderRepository.findByUser(user).stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        user.setTotalShippingFeePaid(totalShippingFee);
        user.setTotalOrderFeePaid(totalOrderFee);
        userRepository.save(user);
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

        Wallet wallet = walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        BigDecimal shippingFee = calculateShippingFee(user, order.getTotalPrice());
        BigDecimal totalPaid = order.getTotalPrice().add(shippingFee);

        if (order.getPaymentMethod().equals(PaymentMethod.E_WALLET)) {
            if (order.getBonusPointsUsed().compareTo(BigDecimal.ZERO) > 0) {
                user.setCurrentBonusPoints(user.getCurrentBonusPoints().add(order.getBonusPointsUsed()));
                wallet.setBonusPoints(wallet.getBonusPoints().add(order.getBonusPointsUsed()));
            }
        }

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());

            if (product.getQuantity() > 0 && product.getStockStatus() == StockStatus.OUT_OF_STOCKS) {
                product.setStockStatus(StockStatus.AVAILABLE);
            }
            productRepository.save(product);
        });

        user.setCancelledOrders(user.getCancelledOrders() + 1);
        user.setTotalShippingFeePaid(user.getTotalShippingFeePaid().subtract(shippingFee));
        user.setTotalOrderFeePaid(user.getTotalOrderFeePaid().subtract(order.getTotalPrice()));
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return new OrderCancellationResponse(
                order.getTotalPrice(),
                order.getShippingFee(),
                totalPaid,
                convertToOrderItemDetails(order.getOrderItems()),
                order.getStatus(),
                LocalDateTime.now(),
                "Refund will be done after minutes.",
                order.getIsBonusPointUsed(),
                order.getBonusPointsUsed()
        );
    }
    public OrderResponse getOrderDetails(Long orderId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to view this order!");
        }

        return convertToOrderResponse(order);
    }
}
