package by.example.fooddelivery.service;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.repository.OrderRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BigDecimal minimumOrderAmount;

    public OrderService(OrderRepository orderRepository, BigDecimal minimumOrderAmount) {
        this.orderRepository = orderRepository;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public Order createOrder(int id, Customer customer) {
        return new Order(id, customer);
    }

    public void AddDish(Order order, Dish dish) {
        order.addDish(dish);
    }

    public void placeOrder(Order order) {
        LOGGER.info("Delivery order received: orderId={}", order.getId());

        if (order.getTotalAmount().compareTo(minimumOrderAmount) < 0) {
            LOGGER.warn(
                    "Delivery rejected: orderId={}, total={}, minimum={}",
                    order.getId(), order.getTotalAmount(), minimumOrderAmount);
            throw new IllegalStateException(
                    "Минимальная сумма заказа: " + minimumOrderAmount);
        }

        try {
            orderRepository.save(order);
        } catch (RuntimeException exception) {
            LOGGER.error(
                    "Delivery processing failed: orderId={}",
                    order.getId(), exception);
            throw exception;
        }

        LOGGER.info(
                "Delivery successfully placed: orderId={}, total={}",
                order.getId(), order.getTotalAmount());
    }
}