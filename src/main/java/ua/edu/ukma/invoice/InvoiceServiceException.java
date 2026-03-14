package ua.edu.ukma.invoice;

public class InvoiceServiceException extends Exception {
    public InvoiceServiceException(String message) {
        super(message);
    }

    public InvoiceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
