package by.example.fooddelivery.model;

import java.math.BigDecimal;

public class Dish {
    private final int id;
    private final String name;
    private final BigDecimal price;

    public Dish(int id, String name, BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Цена блюда не может быть отрицательной");
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
}
