package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureOrder;

import java.util.List;

public interface FeatureOrderMixin extends FeatureTreeMixin {
    FeatureOrder getFeatureOrder();

    default List<Feature> getOrderedFeatures() {
        return getFeatureOrder().apply(this);
    }

    interface Mutator {
        void setFeatureOrder(FeatureOrder featureOrder);
    }
}
