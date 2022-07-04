package org.spldev.model.util;

import java.util.Map;
import java.util.Optional;

/**
 * An object that can be annotated with {@link Attribute} values. For example,
 * attributes are used in {@link org.spldev.model.FeatureModel feature
 * models}, {@link org.spldev.model.Feature features}, and
 * {@link org.spldev.model.Constraint constraints} to store additional
 * metadata.
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

	default boolean hasAttributeValue(Attribute<?> attribute) {
		return getAttributeValue(attribute).isPresent();
	}

	interface Mutator<T extends Attributable> extends org.spldev.model.util.Mutator<T> {
		default <U> void setAttributeValue(Attribute<U> attribute, U value) {
			if (value == null)
				removeAttributeValue(attribute);
			else
				getMutable().getAttributeToValueMap().put(attribute, value);
		}

		default void setArbitraryAttributeValue(Attribute<?> attribute, Object value) {
			if (!attribute.getType().equals(value.getClass()))
				throw new IllegalArgumentException("cannot set attribute of type " + attribute.getType() + " to value of type " + value.getClass());
		}

		default <U> Object removeAttributeValue(Attribute<U> attribute) {
			return getMutable().getAttributeToValueMap().remove(attribute);
		}

		default boolean toggleAttributeValue(Attribute.WithDefaultValue<Boolean> attribute) {
			boolean value = getMutable().getAttributeValue(attribute);
			setAttributeValue(attribute, !value);
			return !value;
		}
	}
}
