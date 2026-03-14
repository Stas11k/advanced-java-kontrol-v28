package ua.edu.ukma.invoice;

import ua.edu.ukma.domain.Order;

public interface InvoiceService {
    String generate(Order order) throws InvoiceServiceException;
}