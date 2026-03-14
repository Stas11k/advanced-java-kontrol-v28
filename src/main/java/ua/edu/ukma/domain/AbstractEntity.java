package ua.edu.ukma.domain;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractEntity {
    private final UUID id;

    protected AbstractEntity() {
        this(UUID.randomUUID());
    }

    protected AbstractEntity(UUID id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    public UUID getId() {
        return id;
    }
}