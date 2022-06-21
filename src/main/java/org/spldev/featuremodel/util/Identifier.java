package org.spldev.featuremodel.util;

import java.util.Objects;

/**
 * Uniquely identifies an {@link org.spldev.featuremodel.util.Identifiable} object.
 * Several implementations are available, implementors are responsible for guaranteeing uniqueness.
 * Is distinguished from {@link org.spldev.util.data.Identifier}, as it can also be parsed from a {@link String}.
 *
 * @author Elias Kuiter
 */
public abstract class Identifier extends org.spldev.util.data.Identifier<Identifiable> {
    protected final Factory factory;

    private Identifier(Factory factory) {
        Objects.requireNonNull(factory);
        this.factory = factory;
    }

    public Factory getFactory() {
        return factory;
    }

    public Identifier getNewIdentifier() {
        return factory.get();
    }

    @Override
    public String toString() {
        return String.valueOf(System.identityHashCode(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public abstract static class Factory implements org.spldev.util.data.Factory<Identifier> {
        abstract public Identifier fromString(String identifierString);
    }

    public static class Counter extends Identifier {
        protected final long counter;

        public Counter(long counter, Factory factory) {
            super(factory);
            this.counter = counter;
        }

        @Override
        public String toString() {
            return String.valueOf(counter);
        }

        public static class Factory extends Identifier.Factory {
            long counter = 0;

            @Override
            public Identifier get() {
                return new Counter(++counter, this);
            }

            @Override
            public Identifier fromString(String identifierString) {
                return new Counter(Long.parseLong(identifierString), this);
            }
        }
    }

    public static class UUID extends Identifier {
        protected final java.util.UUID uuid;

        public UUID(java.util.UUID uuid, Factory factory) {
            super(factory);
            this.uuid = uuid;
        }

        @Override
        public String toString() {
            return uuid.toString();
        }

        public static class Factory extends Identifier.Factory {

            @Override
            public Identifier get() {
                return new UUID(java.util.UUID.randomUUID(), this);
            }
            @Override
            public Identifier fromString(String identifierString) {
                return new UUID(java.util.UUID.fromString(identifierString), this);
            }
        }
    }

    public static Identifier newCounter() {
        return new Counter.Factory().get();
    }

    public static Identifier newUUID() {
        return new UUID.Factory().get();
    }
}
