package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Attributable;
import org.spldev.featuremodel.Attributes;

import java.util.Optional;

public interface AttributeMixin extends Attributable {
     default String getName() {
        return getAttributeValue(Attributes.NAME);
    }

    default void setName(String name) {
        setAttributeValue(Attributes.NAME, name);
    }

    default Optional<String> getDescription() {
        return getAttributeValue(Attributes.DESCRIPTION);
    }

    default void setDescription(String description) {
        setAttributeValue(Attributes.DESCRIPTION, description);
    }
}
