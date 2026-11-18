package by.example.fooddelivery;

import by.example.fooddelivery.model.Customer;
import by.example.fooddelivery.model.Dish;
import by.example.fooddelivery.model.Order;
import by.example.fooddelivery.monitoring.DeliveryMetrics;
import by.example.fooddelivery.monitoring.MetricsServer;
import by.example.fooddelivery.repository.InMemoryOrderRepository;
import by.example.fooddelivery.repository.OrderRepository;
import by.example.fooddelivery.service.OrderService;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        PrometheusMeterRegistry registry =
                new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try (MetricsServer server = new MetricsServer(registry, 8081);
             Scanner scanner = new Scanner(System.in)) {

            DeliveryMetrics metrics = new DeliveryMetrics(registry);

            OrderService service = new OrderService(
                    new InMemoryOrderRepository(),
                    new BigDecimal("20.00"),
                    metrics);

            OrderService failingService = new OrderService(
                    createFailingRepository(),
                    new BigDecimal("20.00"),
                    metrics);

            server.start();

            LOGGER.info("Metrics endpoint: http://localhost:8081/metrics");
            LOGGER.info("Commands: success, invalid, error, exit");

            int orderId = 1;

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();

                if ("exit".equals(command)) {
                    break;
                }

                if ("success".equals(command)) {
                    executeOrder(service, orderId++, "25.00");
                } else if ("invalid".equals(command)) {
                    executeOrder(service, orderId++, "10.00");
                } else if ("error".equals(command)) {
                    executeOrder(failingService, orderId++, "25.00");
                } else {
                    LOGGER.info(
                            "Available commands: success, invalid, error, exit");
                }
            }
        } finally {
            registry.close();
        }
    }

    private static void executeOrder(
            OrderService service, int orderId, String amount) {
        Customer customer = new Customer(
                1, "Demo customer", "Demo delivery address");

        Order order = service.createOrder(orderId, customer);
        service.addDish(
                order,
                new Dish(1, "Pizza", new BigDecimal(amount)));

        try {
            service.placeOrder(order);
        } catch (IllegalStateException exception) {
            LOGGER.info(
                    "Demo operation finished with a problem: {}",
                    exception.getMessage());
        }
    }

    private static OrderRepository createFailingRepository() {
        return new OrderRepository() {
            @Override
            public void save(Order order) {
                throw new IllegalStateException("Storage unavailable");
            }

            @Override
            public Optional<Order> findById(int id) {
                return Optional.empty();
            }
        };
    }
}