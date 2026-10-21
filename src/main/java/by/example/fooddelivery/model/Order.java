package by.example.fooddelivery.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final int id;
    private final Customer customer;
    private final List<Dish> dishes = new ArrayList<>();

    public Order(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public BigDecimal getTotalAmount() {
        return dishes.stream()
                .map(Dish::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
