package ua.edu.ukma.processor;

import ua.edu.ukma.domain.Money;
import ua.edu.ukma.domain.Order;
import ua.edu.ukma.domain.OrderItem;
import ua.edu.ukma.exception.ValidationException;
import ua.edu.ukma.invoice.InvoiceService;
import ua.edu.ukma.payment.BankTransferPayment;

import java.math.BigDecimal;

public class StandardOrderProcessor extends OrderProcessorTemplate {
    private static final Money MIN_ORDER_SUM = Money.of(500, "UAH");
    private static final BigDecimal BANK_TRANSFER_DISCOUNT = new BigDecimal("0.97");

    public StandardOrderProcessor(InvoiceService invoiceService) {
        super(invoiceService);
    }

    @Override
    protected void validate(Order order) throws ValidationException {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        if (order.getItems().length == 0) {
            throw new ValidationException("Order must contain at least one item");
        }

        Money total = Money.of(0, "UAH");
        for (OrderItem item : order.getItems()) {
            total = total.add(item.totalPrice());
        }

        if (total.isLessThan(MIN_ORDER_SUM)) {
            throw new ValidationException("Order sum must be >= 500 UAH");
        }
    }

    @Override
    protected Money applyDiscount(Order order, Money total) {
        if (order.getPaymentMethod() instanceof BankTransferPayment) {
            return total.multiply(BANK_TRANSFER_DISCOUNT);
        }
        return total;
    }

    @Override
    protected Money applyCommission(Order order, Money total) {
        if (order.getPaymentMethod() instanceof BankTransferPayment bankTransferPayment) {
            return bankTransferPayment.applyCommission(total);
        }
        return total;
    }
}