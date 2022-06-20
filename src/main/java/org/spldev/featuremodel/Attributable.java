package org.spldev.featuremodel;

import org.spldev.featuremodel.mixins.CommonAttributesMixin;
import org.spldev.featuremodel.mixins.MutableMixin;

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

	interface Mutator<T extends Attributable> extends MutableMixin.Mutator<T> {
		default <U> void setAttributeValue(Attribute<U> attribute, U value) {
			getMutable().getAttributeToValueMap().put(attribute, value);
		}

		default <U> Object removeAttributeValue(Attribute<U> attribute) {
			return getMutable().getAttributeToValueMap().remove(attribute);
		}
	}
}