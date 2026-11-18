package by.example.fooddelivery.service;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.monitoring.DeliveryMetrics;
import by.example.fooddelivery.repository.OrderRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BigDecimal minimumOrderAmount;
    private final DeliveryMetrics metrics;

    public OrderService(OrderRepository orderRepository, BigDecimal minimumOrderAmount) {
        this(
                orderRepository,
                minimumOrderAmount,
                new DeliveryMetrics(new SimpleMeterRegistry()));
    }

    public OrderService(
            OrderRepository orderRepository,
            BigDecimal minimumOrderAmount,
            DeliveryMetrics metrics) {
        this.orderRepository = orderRepository;
        this.minimumOrderAmount = minimumOrderAmount;
        this.metrics = metrics;
    }

    public Order createOrder(int id, Customer customer) {
        return new Order(id, customer);
    }

    public void addDish(Order order, Dish dish) {
        order.addDish(dish);
    }

    public void placeOrder(Order order) {
        metrics.operationStarted();
        LOGGER.info("Delivery order received: orderId={}", order.getId());

        if (order.getTotalAmount().compareTo(minimumOrderAmount) < 0) {
            metrics.operationFailed();
            LOGGER.warn(
                    "Delivery rejected: orderId={}, total={}, minimum={}",
                    order.getId(), order.getTotalAmount(), minimumOrderAmount);
            throw new IllegalStateException(
                    "Минимальная сумма заказа: " + minimumOrderAmount);
        }

        try {
            orderRepository.save(order);
        } catch (RuntimeException exception) {
            metrics.operationFailed();
            LOGGER.error(
                    "Delivery processing failed: orderId={}",
                    order.getId(), exception);
            throw exception;
        }

        metrics.operationSucceeded();
        LOGGER.info(
                "Delivery successfully placed: orderId={}, total={}",
                order.getId(), order.getTotalAmount());
    }
}