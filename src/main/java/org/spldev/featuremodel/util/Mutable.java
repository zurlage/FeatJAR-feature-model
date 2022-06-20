package org.spldev.featuremodel.util;

import java.util.function.Consumer;

/**
 * An object that can be mutated (i.e., changed) with a {@link Mutator}.
 * While not strictly necessary, this helps to distinguish safe mutation (via a mutator) from potentially unsafe mutation (via immediate API members).
 *
 * @param <T> the type of the mutable object
 * @param <U> the type of the mutator object
 * @author Elias Kuiter
 */
public interface Mutable<T, U extends Mutable.Mutator<T>> {
    interface Mutator<T> {
        T getMutable();
    }

    U getMutator();

    void setMutator(U mutator);

    void invalidate();

    default U mutate() {
        return getMutator();
    }

    default T mutate(Consumer<U> mutatorConsumer) {
        mutatorConsumer.accept(getMutator());
        return getMutator().getMutable();
    }

    default void mutateInternal(Runnable r) {
        try {
            r.run();
        } finally {
            invalidate();
        }
    }

}
