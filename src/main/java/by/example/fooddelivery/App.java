package by.example.fooddelivery;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.repository.InMemoryOrderRepository;
import by.example.fooddelivery.repository.OrderRepository;
import by.example.fooddelivery.service.OrderService;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderService service = new OrderService(repository, new BigDecimal("20.00"));

        Customer customer = new Customer(1, "Иван Иванов", "ул. Ленина, 10");
        Order order = service.createOrder(1001, customer);

        service.addDish(order, new Dish(1, "Пицца", new BigDecimal("15.00")));
        service.addDish(order, new Dish(2, "Салат", new BigDecimal("8.00")));

        service.placeOrder(order);

        LOGGER.info(
                "Заказ {} успешно размещен, всего={}",
                order.getId(), order.getTotalAmount());
    }
}
