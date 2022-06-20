package org.spldev.featuremodel.util;

import java.util.Map;
import java.util.Optional;

/**
 * An object that can be annotated with {@link Attribute} values.
 * For example, attributes are used in {@link org.spldev.featuremodel.FeatureModel feature models},
 * {@link org.spldev.featuremodel.Feature features}, and {@link org.spldev.featuremodel.Constraint constraints} to store additional metadata.
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

	interface Mutator<T extends Attributable> extends Mutable.Mutator<T> {
		default <U> void setAttributeValue(Attribute<U> attribute, U value) {
			getMutable().getAttributeToValueMap().put(attribute, value);
		}

		default <U> Object removeAttributeValue(Attribute<U> attribute) {
			return getMutable().getAttributeToValueMap().remove(attribute);
		}
	}
}