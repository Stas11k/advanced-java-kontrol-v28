package ua.edu.ukma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ua.edu.ukma.domain.*;
import ua.edu.ukma.exception.*;
import ua.edu.ukma.exception.ValidationException;
import ua.edu.ukma.invoice.SimpleInvoiceService;
import ua.edu.ukma.payment.BankTransferPayment;
import ua.edu.ukma.payment.CardPayment;
import ua.edu.ukma.payment.PayPalPayment;
import ua.edu.ukma.payment.PaymentMethod;
import ua.edu.ukma.processor.StandardOrderProcessor;
import ua.edu.ukma.repository.InMemoryOrderRepository;
import ua.edu.ukma.service.OrderService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {
    private InMemoryOrderRepository repository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        service = new OrderService(repository, new StandardOrderProcessor(new SimpleInvoiceService()));
    }

    @Test
    void shouldProcessCardOrderSuccessfully() throws Exception {
        Order order = createOrder(new CardPayment(), 5_000, "user@example.com");
        service.create(order);
        service.process(order.getId());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertNotNull(order.getInvoiceNumber());
        assertEquals(Money.of(5_000, "UAH"), order.getPayableAmount());
    }

    @Test
    void shouldApplyDiscountAndCommissionForBankTransfer() throws Exception {
        Order order = createOrder(new BankTransferPayment(), 10_000, "user@example.com");
        service.create(order);
        service.process(order.getId());
        assertEquals(Money.of(9_942.50, "UAH"), order.getPayableAmount());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldReturnOptionalWhenOrderExists() {
        Order order = createOrder(new CardPayment(), 1_000, "user@example.com");
        service.create(order);
        Optional<Order> found = service.findById(order.getId());
        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenOrderDoesNotExist() {
        assertTrue(service.findById(java.util.UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldThrowValidationExceptionWhenOrderSumBelow500() {
        Order order = createOrder(new CardPayment(), 499, "user@example.com");
        service.create(order);
        assertThrows(ValidationException.class, () -> service.process(order.getId()));
    }

    @Test
    void shouldThrowPaymentExceptionWhenCardLimitExceeded() {
        Order order = createOrder(new CardPayment(), 35_001, "user@example.com");
        service.create(order);
        assertThrows(PaymentException.class, () -> service.process(order.getId()));
    }

    @Test
    void shouldThrowPaymentExceptionWhenPayPalAmountBelowMinimum() {
        OrderItem[] items = { new OrderItem("Single", 1, Money.of(500, "UAH")) };
        Order order = new Order(new Customer("User", new Email("user@example.com")), items, new PayPalPayment()) {
            @Override
            public Money getPayableAmount() {
                return super.getPayableAmount();
            }
        };
        service.create(order);
        PayPalPayment payPalPayment = new PayPalPayment();
        assertThrows(PaymentException.class, () -> payPalPayment.pay(Money.of(399, "UAH"), new Email("user@example.com")));
    }

    @Test
    void shouldThrowInvoiceGenerationException() {
        Order order = createOrder(new CardPayment(), 5_000, "fail-invoice@example.com");
        service.create(order);
        assertThrows(InvoiceGenerationException.class, () -> service.process(order.getId()));
    }

    @Test
    void constructorShouldMakeDefensiveCopy() {
        OrderItem[] items = { new OrderItem("Phone", 1, Money.of(1_000, "UAH")) };
        Order order = new Order(new Customer("User", new Email("user@example.com")), items, new CardPayment());
        items[0] = new OrderItem("Hacked", 1, Money.of(9_999, "UAH"));
        assertEquals("Phone", order.getItems()[0].getProductName());
    }

    @Test
    void getterShouldReturnDefensiveCopy() {
        Order order = createOrder(new CardPayment(), 1_000, "user@example.com");
        OrderItem[] copy = order.getItems();
        copy[0] = new OrderItem("Changed", 1, Money.of(10, "UAH"));
        assertNotEquals("Changed", order.getItems()[0].getProductName());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 100, 250, 499.99})
    void shouldRejectInvalidOrderTotals(double amount) {
        Order order = createOrder(new CardPayment(), amount, "user@example.com");
        service.create(order);
        assertThrows(ValidationException.class, () -> service.process(order.getId()));
    }

    private Order createOrder(PaymentMethod paymentMethod, double totalAmount, String email) {
        OrderItem[] items = { new OrderItem("Item", 1, Money.of(totalAmount, "UAH")) };
        return new Order(new Customer("User", new Email(email)), items, paymentMethod);
    }
}
