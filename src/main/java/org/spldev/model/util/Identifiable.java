package org.spldev.model.util;

/**
 * An object that is uniquely identified by an {@link Identifier}. Can generate
 * new identifiers with the identifier's {@link Identifier.Factory}.
 *
 * @author Elias Kuiter
 */
public interface Identifiable {
	Identifier getIdentifier();

	default Identifier getNewIdentifier() {
		return getIdentifier().getFactory().get();
	}
}
