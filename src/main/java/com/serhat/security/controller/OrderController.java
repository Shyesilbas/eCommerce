package com.serhat.security.controller;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.service.order.cancellation.OrderCancellationService;
import com.serhat.security.service.order.creation.OrderCreationService;
import com.serhat.security.service.order.details.OrderDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderCreationService orderCreationService;
    private final OrderCancellationService orderCancellationService;
    private final OrderDetailsService orderDetailsService;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(orderCreationService.createOrder(orderRequest));
    }

    @GetMapping("/list-order")
    public ResponseEntity<Page<OrderResponse>>getOrdersByUser( Pageable pageable) {
        return ResponseEntity.ok(orderDetailsService.getOrdersByUser( pageable));
    }

    @GetMapping("/order-detail")
    public ResponseEntity<OrderResponse> orderDetail(@RequestParam Long orderId){
        return ResponseEntity.ok(orderDetailsService.getOrderDetails(orderId));
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<OrderCancellationResponse> cancelOrder(@RequestParam Long orderId){
        return ResponseEntity.ok(orderCancellationService.cancelOrder(orderId));
    }

}
