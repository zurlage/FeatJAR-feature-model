package org.spldev.featuremodel.util;

/**
 * An object that can mutate a {@link Mutable}.
 *
 * @param <T> the type of the mutable object
 * @author Elias Kuiter
 */
public interface Mutator<T> {
    T getMutable();
}
