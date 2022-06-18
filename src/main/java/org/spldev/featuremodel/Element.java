package org.spldev.featuremodel;

import java.util.*;

/**
 * Feature model element
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class Element implements Identifiable, Attributable {
	protected final Identifier<?> identifier;
	protected final Map<Attribute<?>, Object> attributeToValueMap = new HashMap<>();

	public Element(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		this.identifier = identifier;
	}

	public Identifier<?> getIdentifier() {
		return identifier;
	}

	@Override
	public Map<Attribute<?>, Object> getAttributeToValueMap() {
		return attributeToValueMap;
	}
}
