package org.spldev.featuremodel.util;

import java.util.function.Consumer;

/**
 * An object that can be mutated (i.e., changed) with a {@link Mutator}. While
 * not strictly necessary, this helps to distinguish safe mutation (via a
 * mutator) from potentially unsafe mutation (via immediate API members). To
 * mutate an object o, call o.mutate(). To mutate a copy of o, call
 * o.clone().mutate().
 *
 * @param <T> the type of the mutable object
 * @param <U> the type of the mutator object
 * @author Elias Kuiter
 */
public interface Mutable<T, U extends Mutator<T>> {
	U getMutator();

	void setMutator(U mutator);

	default void finishInternalMutation() {

	}

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
			finishInternalMutation();
		}
	}

}
