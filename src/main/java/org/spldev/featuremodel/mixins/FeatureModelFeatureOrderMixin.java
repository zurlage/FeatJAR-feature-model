package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureOrder;

import java.util.List;

/**
 * Implements a {@link FeatureModel} mixin for considering a
 * {@link FeatureOrder}.
 *
 * @author Elias Kuiter
 */
public interface FeatureModelFeatureOrderMixin extends FeatureModelFeatureTreeMixin {
	FeatureOrder getFeatureOrder();

	default List<Feature> getOrderedFeatures() {
		return getFeatureOrder().apply(this);
	}

	interface Mutator {
		void setFeatureOrder(FeatureOrder featureOrder);
	}
}
