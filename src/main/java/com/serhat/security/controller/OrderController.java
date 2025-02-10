package com.serhat.security.controller;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.service.order.OrderCancellationService;
import com.serhat.security.service.order.OrderCreationService;
import com.serhat.security.service.order.OrderDetailsService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest , HttpServletRequest request){
        return ResponseEntity.ok(orderCreationService.createOrder(request, orderRequest));
    }

    @GetMapping("/list-order")
    public ResponseEntity<Page<OrderResponse>>getOrdersByUser(HttpServletRequest request, Pageable pageable) {
        return ResponseEntity.ok(orderDetailsService.getOrdersByUser(request, pageable));
    }

    @GetMapping("/order-detail")
    public ResponseEntity<OrderResponse> orderDetail(@RequestParam Long orderId,HttpServletRequest request){
        return ResponseEntity.ok(orderDetailsService.getOrderDetails(orderId,request));
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<OrderCancellationResponse> cancelOrder(HttpServletRequest request , @RequestParam Long orderId){
        return ResponseEntity.ok(orderCancellationService.cancelOrder(orderId, request));
    }

}
