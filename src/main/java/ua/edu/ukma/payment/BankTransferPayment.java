package ua.edu.ukma.payment;

import ua.edu.ukma.domain.Email;
import ua.edu.ukma.domain.Money;
import ua.edu.ukma.exception.PaymentException;

import java.math.BigDecimal;
import java.util.Objects;

public class BankTransferPayment implements PaymentMethod {
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.025");

    @Override
    public void pay(Money amount, Email customerEmail) throws PaymentException, PaymentGatewayException {
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(customerEmail, "customerEmail cannot be null");

        if (amount.getAmount().signum() <= 0) {
            throw new PaymentException("Bank transfer amount must be positive");
        }

        if (customerEmail.getValue().contains("fail-bank")) {
            throw new PaymentGatewayException("Bank API is unavailable");
        }
    }

    public Money applyCommission(Money amount) {
        Objects.requireNonNull(amount, "amount cannot be null");
        return amount.multiply(BigDecimal.ONE.add(COMMISSION_RATE));
    }

    @Override
    public String getName() {
        return "BankTransferPayment";
    }
}