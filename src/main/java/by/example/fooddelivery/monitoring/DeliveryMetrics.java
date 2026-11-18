package by.example.fooddelivery.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class DeliveryMetrics {
    private final Counter operations;
    private final Counter successfulOperations;
    private final Counter errors;
    private final Counter createdOrders;

    public DeliveryMetrics(MeterRegistry registry) {
        operations = Counter.builder("application.operations")
                .description("Total delivery operation attempts")
                .register(registry);

        successfulOperations = Counter.builder("application.operations.success")
                .description("Successful delivery operations")
                .register(registry);

        errors = Counter.builder("application.operations.error")
                .description("Rejected or failed delivery operations")
                .register(registry);

        createdOrders = Counter.builder("food.delivery.orders.created")
                .description("Successfully created food delivery orders")
                .register(registry);
    }

    public void operationStarted() {
        operations.increment();
    }

    public void operationSucceeded() {
        successfulOperations.increment();
        createdOrders.increment();
    }

    public void operationFailed() {
        errors.increment();
    }
}