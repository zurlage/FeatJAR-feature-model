package org.spldev.model.mixins;

import org.spldev.model.Feature;
import org.spldev.model.FeatureModel;
import org.spldev.model.FeatureOrder;

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
