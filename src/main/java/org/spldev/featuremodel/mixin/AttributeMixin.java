package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Attributable;
import org.spldev.featuremodel.Attributes;
import org.spldev.featuremodel.FeatureModel;

import java.util.Optional;

public interface AttributeMixin extends Attributable {
     default String getName() {
        return getAttributeValue(Attributes.NAME);
    }
    
    default Optional<String> getDescription() {
        return getAttributeValue(Attributes.DESCRIPTION);
    }

    interface Mutator {
         FeatureModel getFeatureModel();
        default void setName(String name) {
            getFeatureModel().setAttributeValue(Attributes.NAME, name);
        }

        default void setDescription(String description) {
            getFeatureModel().setAttributeValue(Attributes.DESCRIPTION, description);
        }
    }
}
