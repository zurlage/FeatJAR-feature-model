package org.spldev.featuremodel.mixins;

import java.util.function.Consumer;
import java.util.function.Function;

public interface MutableMixin<T, U extends MutableMixin.Mutator<T>> {
    interface Mutator<T> {
        T getMutable();
    }

    U getMutator();

    default T mutate(Consumer<U> mutatorConsumer) {
        mutatorConsumer.accept(getMutator());
        return getMutator().getMutable();
    }

    default <V> V mutateAndReturn(Function<U, V> mutatorFunction) {
        return mutatorFunction.apply(getMutator());
    }

    default void mutateUnsafely(Runnable r) {
        try {
            r.run();
        } finally {
            //invalidateCaches();
        }
    }
}
