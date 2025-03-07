package com.serhat.security.service.order.cancellation;

import com.serhat.security.dto.response.OrderCancellationResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.component.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Lazy
public class OrderCancellationServiceImpl implements OrderCancellationService {
    private final OrderCancellationProcessor orderCancellationProcessor;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public OrderCancellationResponse cancelOrder(Long orderId) {
        Order order = orderCancellationProcessor.processCancellation(orderId);
        return orderMapper.toOrderCancellationResponse(order, order.getTotalPaid());
    }
}