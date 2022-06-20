package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.Attributes;
import org.spldev.featuremodel.util.Attributable;

import java.util.Optional;

/**
 * Implements accessors for commonly used {@link Attribute attributes}.
 * For example, all {@link org.spldev.featuremodel.FeatureModel feature models},
 * {@link org.spldev.featuremodel.Feature features}, and {@link org.spldev.featuremodel.Constraint constraints} can have names and descriptions.
 *
 * @author Elias Kuiter
 */
public interface CommonAttributesMixin extends Attributable {
     default String getName() {
        return getAttributeValue(Attributes.NAME);
    }

    default Optional<String> getDescription() {
        return getAttributeValue(Attributes.DESCRIPTION);
    }

    interface Mutator<T extends Attributable> extends Attributable.Mutator<T> {
        default void setName(String name) {
            setAttributeValue(Attributes.NAME, name);
        }

        default void setDescription(String description) {
            setAttributeValue(Attributes.DESCRIPTION, description);
        }
    }
}
