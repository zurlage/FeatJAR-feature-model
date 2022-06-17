package org.spldev.featuremodel;

import java.util.Objects;
import java.util.Optional;

/**
 * Attribute
 *
 * @author Elias Kuiter
 */
public class Attribute<T> {
    public static final String DEFAULT_NAMESPACE = "org.spldev.featuremodel";

    public static final Attribute<String> NAME = new Attribute<>("name");
    public static final Attribute<String> DESCRIPTION = new Attribute<>("description");
    public static final Attribute<Boolean> HIDDEN = new Attribute<>("hidden", false);
    public static final Attribute<Boolean> ABSTRACT = new Attribute<>("abstract", false);

    protected final String namespace;
    protected final String name;

    protected final T defaultValue;

    public Attribute(String namespace, String name, T defaultValue) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(name);
        this.namespace = namespace;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Attribute(String namespace, String name) {
        this(namespace, name, null);
    }

    public Attribute(String name, T defaultValue) {
        this(DEFAULT_NAMESPACE, name, defaultValue);
    }

    public Attribute(String name) {
        this(DEFAULT_NAMESPACE, name, null);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public Optional<T> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return namespace.equals(attribute.namespace) && name.equals(attribute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }
}
