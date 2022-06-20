package org.spldev.featuremodel;

import java.util.Objects;

public class Identifier<T> {
    protected T id;

    protected Factory<T> factory;

    private Identifier(T id, Factory<T> factory) {
        Objects.requireNonNull(factory);
        this.id = id;
        this.factory = factory;
    }

    public Factory<T> getFactory() {
        return factory;
    }

    public Identifier<?> getNewIdentifier() {
        return factory.get();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public abstract static class Factory<T> implements org.spldev.util.data.Factory<Identifier<T>> {

        abstract public Identifier<T> fromString(String identifierString);

        public static class Counter extends Factory<Long> {
            long counter = 0;

            @Override
            public Identifier<Long> get() {
                return new Identifier<>(++counter, this);
            }

            @Override
            public Identifier<Long> fromString(String identifierString) {
                return new Identifier<>(Long.valueOf(identifierString), this);
            }
        }

        public static class UUID extends Factory<java.util.UUID> {

            @Override
            public Identifier<java.util.UUID> get() {
                return new Identifier<>(java.util.UUID.randomUUID(), this);
            }
            @Override
            public Identifier<java.util.UUID> fromString(String identifierString) {
                return new Identifier<>(java.util.UUID.fromString(identifierString), this);
            }
        }
    }

    public static Identifier<?> newCounter() {
        return new Factory.Counter().get();
    }
}
