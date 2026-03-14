package ua.edu.ukma.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
    private final String productName;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(String productName, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        this.productName = Objects.requireNonNull(productName, "productName cannot be null");
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice cannot be null");
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
