package org.spldev.featuremodel;

import java.util.Objects;

/**
 * Feature model element
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Identifiable {
	Identifier<?> getIdentifier();
}
