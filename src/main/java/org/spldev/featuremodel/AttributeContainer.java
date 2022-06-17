package org.spldev.featuremodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Attribute container
 *
 * @author Elias Kuiter
 */
public abstract class AttributeContainer {
	protected Map<Attribute<?>, Object> attributeValues = new HashMap<>();

	public Set<Attribute<?>> getDefinedAttributes() {
		return attributeValues.keySet();
	}

	abstract public Set<Attribute<?>> getDefinableAttributes();

	public <T> Optional<T> getAttributeValue(Attribute<T> attribute) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		final T retrievedAttribute = (T) attributeValues.get(attribute);
		return retrievedAttribute == null ? attribute.getDefaultValue() : Optional.of(retrievedAttribute);
	}

	public <T> void setAttributeValue(Attribute<T> attribute, T value) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		attributeValues.put(attribute, value);
	}

	public <T> Object remove(Attribute<T> attribute) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		return attributeValues.remove(attribute);
	}
}
