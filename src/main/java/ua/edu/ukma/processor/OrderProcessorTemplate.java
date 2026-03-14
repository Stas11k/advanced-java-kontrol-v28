package ua.edu.ukma.processor;

import ua.edu.ukma.domain.Money;
import ua.edu.ukma.domain.Order;
import ua.edu.ukma.domain.OrderItem;
import ua.edu.ukma.exception.AppException;
import ua.edu.ukma.exception.InvoiceGenerationException;
import ua.edu.ukma.exception.PaymentException;
import ua.edu.ukma.invoice.InvoiceService;
import ua.edu.ukma.invoice.InvoiceServiceException;
import ua.edu.ukma.payment.PaymentGatewayException;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OrderProcessorTemplate {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    private final InvoiceService invoiceService;

    protected OrderProcessorTemplate(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public final void process(Order order) throws AppException {
        try {
            logger.info("Start processing order " + order.getId());
            validate(order);
            Money payable = calculate(order);
            order.setPayableAmount(payable);
            logger.info("Calculated payable amount: " + payable);
            pay(order, payable);
            order.markPaid();
            logger.info("Payment completed for order " + order.getId());
            String invoiceNumber = generateInvoice(order);
            order.markInvoiceSent(invoiceNumber);
            logger.info("Invoice generated: " + invoiceNumber);
            ship(order);
            complete(order);
            notifyCustomer(order);
            logger.info("Order fully processed: " + order.getId());
        } catch (PaymentException e) {
            logger.warning("Business payment problem: " + e.getMessage());
            throw e;
        } catch (InvoiceGenerationException e) {
            logger.warning("Invoice generation problem: " + e.getMessage());
            throw e;
        } catch (AppException e) {
            logger.warning("Business validation problem: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected infrastructure failure", e);
            throw new AppException("Unexpected processing failure", e);
        }
    }

    protected abstract void validate(Order order) throws AppException;

    protected Money calculate(Order order) {
        Money total = baseTotal(order);
        total = applyDiscount(order, total);
        total = applyCommission(order, total);
        return total;
    }

    protected void pay(Order order, Money payable) throws PaymentException {
        try {
            order.getPaymentMethod().pay(payable, order.getCustomer().getEmail());
        } catch (PaymentGatewayException e) {
            throw new PaymentException("Payment infrastructure failed", e);
        }
    }

    protected String generateInvoice(Order order) throws InvoiceGenerationException {
        try {
            return invoiceService.generate(order);
        } catch (InvoiceServiceException e) {
            throw new InvoiceGenerationException("Could not generate invoice", e);
        }
    }

    protected void ship(Order order) {
        order.markShipped();
        logger.info("Order shipped: " + order.getId());
    }

    protected void complete(Order order) {
        order.markDelivered();
        logger.info("Order delivered: " + order.getId());
    }

    protected void notifyCustomer(Order order) {
        logger.info("Notification sent to " + order.getCustomer().getEmail());
    }

    private Money baseTotal(Order order) {
        Money total = Money.of(0, "UAH");
        for (OrderItem item : order.getItems()) {
            total = total.add(item.totalPrice());
        }
        return total;
    }

    protected Money applyDiscount(Order order, Money total) {
        return total;
    }

    protected Money applyCommission(Order order, Money total) {
        return total;
    }
}