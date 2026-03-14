package ua.edu.ukma.service;

import ua.edu.ukma.domain.Order;
import ua.edu.ukma.exception.AppException;
import ua.edu.ukma.exception.OrderNotFoundException;
import ua.edu.ukma.processor.OrderProcessorTemplate;
import ua.edu.ukma.repository.OrderRepository;

import java.util.Optional;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProcessorTemplate orderProcessor;

    public OrderService(OrderRepository orderRepository, OrderProcessorTemplate orderProcessor) {
        this.orderRepository = orderRepository;
        this.orderProcessor = orderProcessor;
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public void process(UUID orderId) throws AppException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        orderProcessor.process(order);
        orderRepository.save(order);
    }

    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}