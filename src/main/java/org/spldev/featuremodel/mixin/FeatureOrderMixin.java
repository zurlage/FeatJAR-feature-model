package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureOrder;

import java.util.List;

public interface FeatureOrderMixin extends FeatureTreeMixin {
    FeatureOrder getFeatureOrder();

    void setFeatureOrder(FeatureOrder featureOrder);

    default List<Feature> getOrderedFeatures() {
        return getFeatureOrder().apply(this);
    }
}
