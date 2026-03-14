package ua.edu.ukma.payment;

import ua.edu.ukma.domain.Email;
import ua.edu.ukma.domain.Money;
import ua.edu.ukma.exception.PaymentException;

public interface PaymentMethod {
    void pay(Money amount, Email customerEmail) throws PaymentException, PaymentGatewayException;
    String getName();
}
