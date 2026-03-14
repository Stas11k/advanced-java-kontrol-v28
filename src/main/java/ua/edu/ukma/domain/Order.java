package ua.edu.ukma.domain;

import ua.edu.ukma.payment.PaymentMethod;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Order extends AbstractEntity {
    private final Customer customer;
    private final OrderItem[] items;
    private final PaymentMethod paymentMethod;
    private OrderStatus status;
    private Money payableAmount;
    private String invoiceNumber;

    public Order(Customer customer, OrderItem[] items, PaymentMethod paymentMethod) {
        this(UUID.randomUUID(), customer, items, paymentMethod, OrderStatus.NEW);
    }

    public Order(UUID id, Customer customer, OrderItem[] items, PaymentMethod paymentMethod, OrderStatus status) {
        super(id);
        this.customer = Objects.requireNonNull(customer, "customer cannot be null");
        this.items = defensiveCopy(items);
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }

    private OrderItem[] defensiveCopy(OrderItem[] source) {
        if (source == null) throw new IllegalArgumentException("items cannot be null");
        OrderItem[] copy = Arrays.copyOf(source, source.length);
        for (OrderItem item : copy) {
            if (item == null) throw new IllegalArgumentException("items cannot contain null");
        }
        return copy;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getPayableAmount() {
        return payableAmount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setPayableAmount(Money payableAmount) {
        this.payableAmount = Objects.requireNonNull(payableAmount, "payableAmount cannot be null");
    }

    public void markPaid() {
        ensureStatus(OrderStatus.NEW);
        this.status = OrderStatus.PAID;
    }

    public void markInvoiceSent(String invoiceNumber) {
        ensureStatus(OrderStatus.PAID);
        this.invoiceNumber = Objects.requireNonNull(invoiceNumber, "invoiceNumber cannot be null");
        this.status = OrderStatus.INVOICE_SENT;
    }

    public void markShipped() {
        ensureStatus(OrderStatus.INVOICE_SENT);
        this.status = OrderStatus.SHIPPED;
    }

    public void markDelivered() {
        ensureStatus(OrderStatus.SHIPPED);
        this.status = OrderStatus.DELIVERED;
    }

    private void ensureStatus(OrderStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Expected status " + expected + " but was " + status);
        }
    }
}