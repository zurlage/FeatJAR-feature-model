package org.spldev.featuremodel;

/**
 * Feature model element
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Identifiable {
	Identifier<?> getIdentifier();

	default Identifier<?> getNewIdentifier() {
		return getIdentifier().getFactory().get();
	}
}
