package by.example.fooddelivery.repository;

import by.example.fooddelivery.model.Order;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(int id);
}
