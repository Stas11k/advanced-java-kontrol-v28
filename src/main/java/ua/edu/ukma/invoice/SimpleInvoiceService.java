package ua.edu.ukma.invoice;

import ua.edu.ukma.domain.Order;

public class SimpleInvoiceService implements InvoiceService {
    @Override
    public String generate(Order order) throws InvoiceServiceException {
        if (order.getCustomer().getEmail().getValue().contains("fail-invoice")) {
            throw new InvoiceServiceException("Invoice provider rejected request");
        }
        return "INV-" + order.getId().toString().substring(0, 8).toUpperCase();
    }
}
