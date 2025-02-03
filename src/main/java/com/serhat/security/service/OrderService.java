package com.serhat.security.service;

import com.serhat.security.dto.object.AddressDto;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderItemDetails;
import com.serhat.security.dto.response.OrderResponse;
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
import java.math.RoundingMode;
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
    private final TransactionRepository transactionRepository;
    private final DiscountCodeService discountCodeService;
    private final DiscountCodeRepository discountCodeRepository;
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
            default -> {
                return BigDecimal.ZERO;
            }
        }
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
        BigDecimal bonusWon = calculateBonusPoints(order.getUser(), order.getTotalPrice());
        BigDecimal shippingFee = calculateShippingFee(order.getUser(), order.getTotalPrice());
        BigDecimal totalPaid = order.getTotalPrice().add(shippingFee);

        if (order.getTotalDiscount() != null && order.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0) {
            totalPaid = totalPaid.subtract(order.getTotalDiscount());
        }

        Long discountId = order.getDiscountCode() != null ? order.getDiscountCode().getId() : null;
        BigDecimal discountRate = order.getDiscountCode() != null ? BigDecimal.valueOf(order.getDiscountCode().getDiscountRate().getPercentage()) : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getTotalDiscount() != null ? order.getTotalDiscount() : BigDecimal.ZERO;

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .totalQuantity(order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum())
                .bonusWon(bonusWon)
                .shippingFee(shippingFee)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .notes(order.getNotes())
                .orderItems(orderItems)
                .shippingAddress(shippingAddress)
                .totalPaid(totalPaid)
                .discountId(discountId)
                .discountRate(discountRate)
                .discountAmount(discountAmount)
                .build();
    }


    @Transactional
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = tokenInterface.getUserFromToken(request);

        if (!isAddressBelongsToUser(orderRequest.shippingAddressId(), user.getUserId())) {
            throw new AddressNotBelongToUserException("Shipping address does not belong to the user!");
        }

        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);
        if (shoppingCards.isEmpty()) {
            throw new EmptyShoppingCardException("No products in the shopping cart!");
        }

        BigDecimal totalPrice = shoppingCards.stream()
                .map(sc -> sc.getProduct().getPrice().multiply(new BigDecimal(sc.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = calculateShippingFee(user, totalPrice);
        BigDecimal finalPrice = totalPrice.add(shippingFee);

        BigDecimal bonusPoints = calculateBonusPoints(user, totalPrice);
        user.setBonusPointsWon(user.getBonusPointsWon().add(bonusPoints));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(OrderStatus.APPROVED)
                .shippingAddressId(orderRequest.shippingAddressId())
                .paymentMethod(orderRequest.paymentMethod())
                .notes(orderRequest.notes())
                .updatedAt(LocalDateTime.now())
                .shippingFee(shippingFee)
                .bonusWon(bonusPoints)
                .discountCode(null)
                .totalDiscount(BigDecimal.ZERO)
                .discountRate(DiscountRate.ZERO)
                .discountCodeUsed(false)
                .totalPaid(finalPrice)
                .build();

        orderRepository.save(order);

        List<Transaction> transactions = new ArrayList<>();

        if (orderRequest.paymentMethod() == PaymentMethod.E_WALLET) {
            Wallet wallet = walletRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

            if (wallet.getBalance().compareTo(finalPrice) < 0) {
                throw new InsufficientFundsException("Insufficient funds in wallet");
            }

            wallet.setBalance(wallet.getBalance().subtract(finalPrice));
            wallet.setBonusPoints(wallet.getBonusPoints().add(bonusPoints));

            String paymentDescription = shippingFee.compareTo(BigDecimal.ZERO) > 0
                    ? String.format("Payment for order (including shipping fee: %s)", shippingFee)
                    : "Payment for order";

            Transaction paymentTransaction = new Transaction();
            paymentTransaction.setWallet(wallet);
            paymentTransaction.setUser(user);
            paymentTransaction.setOrder(order);
            paymentTransaction.setAmount(finalPrice);
            paymentTransaction.setTransactionType(TransactionType.PAYMENT);
            paymentTransaction.setTransactionDate(LocalDateTime.now());
            paymentTransaction.setDescription(paymentDescription);

            transactions.add(paymentTransaction);

            Transaction bonusTransaction = new Transaction();
            bonusTransaction.setWallet(wallet);
            bonusTransaction.setUser(user);
            bonusTransaction.setOrder(order);
            bonusTransaction.setAmount(bonusPoints);
            bonusTransaction.setTransactionType(TransactionType.BONUS_GRANTED);
            bonusTransaction.setTransactionDate(LocalDateTime.now());
            bonusTransaction.setDescription("Bonus points granted for order");

            transactions.add(bonusTransaction);

            transactionRepository.saveAll(transactions);
            walletRepository.save(wallet);
        }

        List<OrderItem> orderItems = shoppingCards.stream()
                .map(sc -> {
                    Product product = sc.getProduct();
                    if (product.getQuantity() < sc.getQuantity()) {
                        throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                    }

                    product.setQuantity(product.getQuantity() - sc.getQuantity());

                    if (product.getQuantity() == 0) {
                        product.setStockStatus(StockStatus.OUT_OF_STOCKS);
                    }

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(sc.getQuantity())
                            .price(product.getPrice())
                            .build();
                }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setTransactions(transactions);

        orderRepository.save(order);
        user.setTotalOrders(user.getTotalOrders() + 1);
        shoppingCardRepository.deleteAll(shoppingCards);

        productRepository.saveAll(orderItems.stream().map(OrderItem::getProduct).collect(Collectors.toList()));

        Long discountId = orderRequest.discountId();
        if (discountId != null) {
            if (!user.isActiveDiscountCode()) {
                throw new InvalidDiscountCodeException("User is not eligible to use a discount code!");
            }

            DiscountCode discountCode = discountCodeRepository.findById(discountId)
                    .orElseThrow(() -> new InvalidDiscountCodeException("Invalid discount code"));

            if (discountCode.getUser() != null && !discountCode.getUser().getUserId().equals(user.getUserId())) {
                throw new InvalidDiscountCodeException("This discount code is not valid for the current user!");
            }

            BigDecimal discountAmount = order.getTotalPrice()
                    .multiply(BigDecimal.valueOf(discountCode.getDiscountRate().getPercentage() / 100.0));
            totalPrice = totalPrice.subtract(discountAmount);

            order.setTotalDiscount(discountAmount);
            order.setDiscountRate(discountCode.getDiscountRate());
            order.setDiscountCode(discountCode);
            order.setDiscountCodeUsed(true);
            order.setTotalPaid(totalPrice.add(shippingFee));
            discountCode.setUsed(true);
            user.setActiveDiscountCode(false);
        }

        if (order.getTotalPrice().compareTo(new BigDecimal("800.00")) >= 0) {
            user.setActiveDiscountCode(true);
            discountCodeService.generateDiscountCode(request);
        }

        updateUserTotalFees(user);

        return convertToOrderResponse(order);
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
                        Wallet wallet = walletRepository.findByUser_UserId(order.getUser().getUserId())
                                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

                        wallet.setBalance(wallet.getBalance().add(order.getTotalPrice()));
                        walletRepository.save(wallet);

                        Transaction transaction = new Transaction();
                        transaction.setWallet(wallet);
                        transaction.setUser(order.getUser());
                        transaction.setAmount(order.getTotalPrice());
                        transaction.setTransactionType(TransactionType.CANCEL_REFUND);
                        transaction.setTransactionDate(LocalDateTime.now());
                        transaction.setDescription("Refund for canceled order");

                        transactionRepository.save(transaction);
                        log.info("Order {} REFUNDED: {} added back to wallet", order.getOrderId(), order.getTotalPrice());}
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

        BigDecimal bonusPointsToDeduct = calculateBonusPoints(user, order.getTotalPrice());

        BigDecimal shippingFee = calculateShippingFee(user, order.getTotalPrice());

        BigDecimal totalPaid = order.getTotalPrice().add(shippingFee);

        if (order.getPaymentMethod().equals(PaymentMethod.E_WALLET)) {
            if (user.getBonusPointsWon().compareTo(bonusPointsToDeduct) >= 0) {
                user.setBonusPointsWon(user.getBonusPointsWon().subtract(bonusPointsToDeduct));
            } else {
                user.setBonusPointsWon(BigDecimal.ZERO);
            }

            if (wallet.getBonusPoints().compareTo(bonusPointsToDeduct) >= 0) {
                wallet.setBonusPoints(wallet.getBonusPoints().subtract(bonusPointsToDeduct));
            } else {
                wallet.setBonusPoints(BigDecimal.ZERO);
            }

            wallet.setBalance(wallet.getBalance().add(totalPaid));
            walletRepository.save(wallet);

            Transaction refundTransaction = new Transaction();
            refundTransaction.setWallet(wallet);
            refundTransaction.setUser(user);
            refundTransaction.setOrder(order);
            refundTransaction.setAmount(totalPaid);
            refundTransaction.setTransactionType(TransactionType.CANCEL_REFUND);
            refundTransaction.setTransactionDate(LocalDateTime.now());
            refundTransaction.setDescription(String.format("Refund for canceled order, including shipping fee: %s", shippingFee));

            transactionRepository.save(refundTransaction);

            log.info("Order {} refunded: {} returned to user wallet.", order.getOrderId(), totalPaid);
        }

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());

            if (product.getQuantity() > 0 && product.getStockStatus() == StockStatus.OUT_OF_STOCKS) {
                product.setStockStatus(StockStatus.AVAILABLE);
            }

            productRepository.save(product);
            log.info("Product {} stock restored: {} items now available.", product.getName(), product.getQuantity());
        });

        user.setCancelledOrders(user.getCancelledOrders() + 1);

        order.setStatus(OrderStatus.CANCELLED);
        user.setTotalShippingFeePaid(user.getTotalShippingFeePaid().subtract(shippingFee));
        user.setTotalOrderFeePaid(user.getTotalOrderFeePaid().subtract(order.getTotalPrice()));
        orderRepository.save(order);

        log.info("Order {} has been canceled by user {}. {} bonus points deducted.", order.getOrderId(), user.getUsername(), bonusPointsToDeduct);


        return new OrderCancellationResponse(
                order.getTotalPrice(),
                order.getShippingFee(),
                totalPaid,
                convertToOrderItemDetails(order.getOrderItems()),
                order.getStatus(),
                LocalDateTime.now(),
                "Refund fee will be deposited into your wallet as soon as possible."
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
