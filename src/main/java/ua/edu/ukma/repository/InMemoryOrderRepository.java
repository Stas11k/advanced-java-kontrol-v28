package ua.edu.ukma.repository;

import ua.edu.ukma.domain.Order;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> storage = new HashMap<>();

    @Override
    public Order save(Order order) {
        storage.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }
}
