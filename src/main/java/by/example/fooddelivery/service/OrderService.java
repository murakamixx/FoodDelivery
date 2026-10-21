package by.example.fooddelivery.service;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.repository.OrderRepository;
import java.math.BigDecimal;

public class OrderService {
    private final OrderRepository orderRepository;
    private final BigDecimal minimumOrderAmount;

    public OrderService(OrderRepository orderRepository, BigDecimal minimumOrderAmount) {
        this.orderRepository = orderRepository;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public Order createOrder(int id, Customer customer) {
        return new Order(id, customer);
    }

    public void addDish(Order order, Dish dish) {
        order.addDish(dish);
    }

    public void placeOrder(Order order) {
        if (order.getTotalAmount().compareTo(minimumOrderAmount) < 0) {
            throw new IllegalStateException("Минимальная сумма заказа: " + minimumOrderAmount);
        }
        orderRepository.save(order);
    }
}
