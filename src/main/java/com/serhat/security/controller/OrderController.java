package com.serhat.security.controller;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService service;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest , HttpServletRequest request){
        return ResponseEntity.ok(service.createOrder(request, orderRequest));
    }

    @GetMapping("/list-order")
    public ResponseEntity<List<OrderResponse>> listOrders(HttpServletRequest request){
        return ResponseEntity.ok(service.getOrdersByUser(request));
    }

    @GetMapping("/order-detail")
    public ResponseEntity<OrderResponse> orderDetail(@RequestParam Long orderId,HttpServletRequest request){
        return ResponseEntity.ok(service.getOrderDetails(orderId,request));
    }

}
