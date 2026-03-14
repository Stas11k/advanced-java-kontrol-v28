package ua.edu.ukma.exception;

public class InvoiceGenerationException extends AppException {
    public InvoiceGenerationException(String message) {
        super(message);
    }

    public InvoiceGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
