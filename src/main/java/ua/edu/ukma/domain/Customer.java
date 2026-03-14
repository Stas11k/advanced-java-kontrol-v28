package ua.edu.ukma.domain;

import java.util.Objects;
import java.util.UUID;

public class Customer extends AbstractEntity {
    private final String fullName;
    private final Email email;

    public Customer(String fullName, Email email) {
        this(UUID.randomUUID(), fullName, email);
    }

    public Customer(UUID id, String fullName, Email email) {
        super(id);
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
    }

    public String getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }
}
