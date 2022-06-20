package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.Attributable;
import org.spldev.featuremodel.Attributes;

import java.util.Optional;

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
