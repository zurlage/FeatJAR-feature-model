package org.spldev.featuremodel;

public class Identifier<T> {
    T id;

    private Identifier(T id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public abstract static class Factory<T> implements org.spldev.util.data.Factory<Identifier<T>> {
        abstract public Identifier<T> fromString(String identifierString);

        public static class UUID extends Factory<java.util.UUID> {
            @Override
            public Identifier<java.util.UUID> get() {
                return new Identifier<>(java.util.UUID.randomUUID());
            }

            @Override
            public Identifier<java.util.UUID> fromString(String identifierString) {
                return new Identifier<>(java.util.UUID.fromString(identifierString));
            }
        }
    }
}
