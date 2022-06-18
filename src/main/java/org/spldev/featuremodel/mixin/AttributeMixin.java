package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Attributable;
import org.spldev.featuremodel.Attribute;
import org.spldev.featuremodel.Attributes;

import java.util.Optional;
import java.util.Set;

public interface AttributeMixin extends FeatureModelTreeMixin, Attributable {
    Set<Attribute<?>> getOwnDefinableAttributes();

    Set<Attribute<?>> getOwnDefinableFeatureAttributes();

    Set<Attribute<?>> getOwnDefinableConstraintAttributes();

    default Set<Attribute<?>> getDefinableAttributes() { // todo: not ideal, does not play well with manipulation of FM tree
        if (getFeatureModelTree().isRoot())
            return getOwnDefinableAttributes();
        return getFeatureModelTree().getRoot().getFeatureModel().getDefinableAttributes();
    }

    default Set<Attribute<?>> getDefinableFeatureAttributes() {
        if (getFeatureModelTree().isRoot())
            return getOwnDefinableFeatureAttributes();
        return getFeatureModelTree().getRoot().getFeatureModel().getDefinableFeatureAttributes();
    }

    default Set<Attribute<?>> getDefinableConstraintAttributes() {
        if (getFeatureModelTree().isRoot())
            return getOwnDefinableConstraintAttributes();
        return getFeatureModelTree().getRoot().getFeatureModel().getDefinableConstraintAttributes();
    }

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
