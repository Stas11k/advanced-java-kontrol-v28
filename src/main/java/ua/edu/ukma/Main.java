package ua.edu.ukma;

import ua.edu.ukma.domain.Customer;
import ua.edu.ukma.domain.Email;
import ua.edu.ukma.domain.Money;
import ua.edu.ukma.domain.Order;
import ua.edu.ukma.domain.OrderItem;
import ua.edu.ukma.exception.AppException;
import ua.edu.ukma.invoice.InvoiceService;
import ua.edu.ukma.invoice.SimpleInvoiceService;
import ua.edu.ukma.payment.BankTransferPayment;
import ua.edu.ukma.payment.PaymentMethod;
import ua.edu.ukma.processor.OrderProcessorTemplate;
import ua.edu.ukma.processor.StandardOrderProcessor;
import ua.edu.ukma.repository.InMemoryOrderRepository;
import ua.edu.ukma.repository.OrderRepository;
import ua.edu.ukma.service.OrderService;

public class Main {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        InvoiceService invoiceService = new SimpleInvoiceService();
        OrderProcessorTemplate processor = new StandardOrderProcessor(invoiceService);
        OrderService orderService = new OrderService(repository, processor);

        Customer customer = new Customer("Stas Dubyna", new Email("stas@example.com"));

        OrderItem[] items = new OrderItem[] {
                new OrderItem("Laptop", 1, Money.of(20000, "UAH")),
                new OrderItem("Mouse", 2, Money.of(800, "UAH"))
        };

        PaymentMethod paymentMethod = new BankTransferPayment();
        Order order = new Order(customer, items, paymentMethod);

        try {
            orderService.create(order);
            orderService.process(order.getId());

            System.out.println("Final status: " + order.getStatus());
            System.out.println("Payable amount: " + order.getPayableAmount());
            System.out.println("Invoice: " + order.getInvoiceNumber());
        } catch (AppException e) {
            System.out.println("Processing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}