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
public interface Attributable {
	Map<Attribute<?>, Object> getAttributeToValueMap();
	Set<Attribute<?>> getDefinableAttributes();

	default Set<Attribute<?>> getDefinedAttributes() {
		return getAttributeToValueMap().keySet();
	}

	default <T> Optional<T> getAttributeValue(Attribute<T> attribute) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		return attribute.apply(getAttributeToValueMap());
	}

	default <T> T getAttributeValue(Attribute.WithDefaultValue<T> attribute) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		return attribute.applyWithDefaultValue(getAttributeToValueMap(), this);
	}

	default <T> void setAttributeValue(Attribute<T> attribute, T value) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		getAttributeToValueMap().put(attribute, value);
	}

	default <T> Object removeAttributeValue(Attribute<T> attribute) {
		if (!getDefinableAttributes().contains(attribute))
			throw new IllegalArgumentException();
		return getAttributeToValueMap().remove(attribute);
	}
}