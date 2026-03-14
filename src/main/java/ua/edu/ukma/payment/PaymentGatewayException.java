package ua.edu.ukma.payment;

public class PaymentGatewayException extends Exception {
    public PaymentGatewayException(String message) {
        super(message);
    }

    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
