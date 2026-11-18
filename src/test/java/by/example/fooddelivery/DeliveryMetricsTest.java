package by.example.fooddelivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.monitoring.DeliveryMetrics;
import by.example.fooddelivery.repository.InMemoryOrderRepository;
import by.example.fooddelivery.service.OrderService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DeliveryMetricsTest {
    @Test
    void shouldCountSuccessfulOrder() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        try {
            OrderService service = new OrderService(
                    new InMemoryOrderRepository(),
                    new BigDecimal("20.00"),
                    new DeliveryMetrics(registry));

            Order order = service.createOrder(
                    1, new Customer(1, "Customer", "Address"));

            service.addDish(
                    order,
                    new Dish(1, "Pizza", new BigDecimal("25.00")));

            service.placeOrder(order);

            assertEquals(
                    1.0,
                    registry.get("application.operations").counter().count());

            assertEquals(
                    1.0,
                    registry.get("application.operations.success")
                            .counter().count());

            assertEquals(
                    0.0,
                    registry.get("application.operations.error")
                            .counter().count());

            assertEquals(
                    1.0,
                    registry.get("food.delivery.orders.created")
                            .counter().count());
        } finally {
            registry.close();
        }
    }

    @Test
    void shouldCountRejectedOrder() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        try {
            OrderService service = new OrderService(
                    new InMemoryOrderRepository(),
                    new BigDecimal("20.00"),
                    new DeliveryMetrics(registry));

            Order order = service.createOrder(
                    2, new Customer(1, "Customer", "Address"));

            service.addDish(
                    order,
                    new Dish(1, "Pizza", new BigDecimal("10.00")));

            assertThrows(
                    IllegalStateException.class,
                    () -> service.placeOrder(order));

            assertEquals(
                    1.0,
                    registry.get("application.operations").counter().count());

            assertEquals(
                    0.0,
                    registry.get("application.operations.success")
                            .counter().count());

            assertEquals(
                    1.0,
                    registry.get("application.operations.error")
                            .counter().count());

            assertEquals(
                    0.0,
                    registry.get("food.delivery.orders.created")
                            .counter().count());
        } finally {
            registry.close();
        }
    }
}