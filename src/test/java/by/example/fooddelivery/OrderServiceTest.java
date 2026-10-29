package by.example.fooddelivery;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.repository.InMemoryOrderRepository;
import by.example.fooddelivery.repository.OrderRepository;
import by.example.fooddelivery.service.OrderService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    private final Customer customer = new Customer(1, "Тестовый клиент", "Тестовый адрес");

    @Test
    void shouldPlaceOrderWhenAmountIsEnough() {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderService service = new OrderService(repository, new BigDecimal("20.00"));
        Order order = service.createOrder(1, customer);
        service.addDish(order, new Dish(1, "Пицца", new BigDecimal("25.00")));

        assertDoesNotThrow(() -> service.placeOrder(order));
        assertTrue(repository.findById(1).isPresent());
    }

    @Test
    void shouldRejectOrderWhenAmountIsBelowMinimum() {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderService service = new OrderService(repository, new BigDecimal("20.00"));
        Order order = service.createOrder(2, customer);
        service.addDish(order, new Dish(1, "Пицца", new BigDecimal("15.00")));

        assertThrows(IllegalStateException.class, () -> service.placeOrder(order));
        assertTrue(repository.findById(2).isEmpty());
    }

    @Test
    void shouldPlaceOrderWhenAmountEqualsMinimum() {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderService service = new OrderService(repository, new BigDecimal("20.00"));
        Order order = service.createOrder(3, customer);
        service.addDish(order, new Dish(1, "Пицца", new BigDecimal("20.00")));

        assertDoesNotThrow(() -> service.placeOrder(order));
        assertEquals(new BigDecimal("20.00"), order.getTotalAmount());
    }

    @Test
    void shouldCalculateTotalWhenMultipleDishesAreAdded() {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderService service =
                new OrderService(repository, new BigDecimal("20.00"));

        Order order = service.createOrder(4, customer);

        service.addDish(order,
                new Dish(1, "Пицца", new BigDecimal("25.00")));
        service.addDish(order,
                new Dish(2, "Салат", new BigDecimal("10.00")));

        assertEquals(2, order.getDishes().size());
        assertEquals(
                new BigDecimal("35.00"),
                order.getTotalAmount()
        );
    }
}
