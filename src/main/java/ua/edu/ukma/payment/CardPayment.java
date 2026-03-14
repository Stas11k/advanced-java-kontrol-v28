package ua.edu.ukma.payment;

import ua.edu.ukma.domain.Email;
import ua.edu.ukma.domain.Money;
import ua.edu.ukma.exception.PaymentException;

import java.util.Objects;

public class CardPayment implements PaymentMethod {
    private static final Money LIMIT = Money.of(35_000, "UAH");

    @Override
    public void pay(Money amount, Email customerEmail) throws PaymentException, PaymentGatewayException {
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(customerEmail, "customerEmail cannot be null");

        if (amount.isGreaterThan(LIMIT)) {
            throw new PaymentException("Card payment allows up to 35_000 UAH");
        }

        if (customerEmail.getValue().contains("fail-card")) {
            throw new PaymentGatewayException("Card gateway timeout");
        }
    }

    @Override
    public String getName() {
        return "CardPayment";
    }
}