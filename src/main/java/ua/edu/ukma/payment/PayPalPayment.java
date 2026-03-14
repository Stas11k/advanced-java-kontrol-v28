package ua.edu.ukma.payment;

import ua.edu.ukma.domain.Email;
import ua.edu.ukma.domain.Money;
import ua.edu.ukma.exception.PaymentException;

import java.util.Objects;

public class PayPalPayment implements PaymentMethod {
    private static final Money MINIMUM = Money.of(400, "UAH");

    @Override
    public void pay(Money amount, Email customerEmail) throws PaymentException, PaymentGatewayException {
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(customerEmail, "customerEmail cannot be null");

        if (amount.isLessThan(MINIMUM)) {
            throw new PaymentException("PayPal accepts amounts from 400 UAH");
        }

        if (customerEmail.getValue().contains("fail-paypal")) {
            throw new PaymentGatewayException("PayPal gateway unavailable");
        }
    }

    @Override
    public String getName() {
        return "PayPalPayment";
    }
}