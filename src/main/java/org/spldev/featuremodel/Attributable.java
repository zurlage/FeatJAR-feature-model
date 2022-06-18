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

	default <T> Optional<T> getAttributeValue(Attribute<T> attribute) {
		return attribute.apply(getAttributeToValueMap());
	}

	default <T> T getAttributeValue(Attribute.WithDefaultValue<T> attribute) {
		return attribute.applyWithDefaultValue(getAttributeToValueMap(), this);
	}

	default <T> void setAttributeValue(Attribute<T> attribute, T value) {
		getAttributeToValueMap().put(attribute, value);
	}

	default <T> Object removeAttributeValue(Attribute<T> attribute) {
		return getAttributeToValueMap().remove(attribute);
	}
}