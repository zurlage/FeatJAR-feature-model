package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.*;
import org.spldev.featuremodel.util.Identifier;

import java.util.*;

/**
 * Cache. assumes that features/constraints are only added/deleted through the feature model class, not manually
 *
 * @author Elias Kuiter
 */
public interface FeatureModelCacheMixin extends FeatureModelFeatureTreeMixin, FeatureModelConstraintMixin {
	Map<Identifier<?>, Element> getElementCache();
	Set<Feature> getFeatureCache();
	Set<FeatureModel> getFeatureModelCache();

	private void invalidateElement(Element element) {
		if (getElementCache().get(element.getIdentifier()) != null)
			throw new RuntimeException("duplicate identifier " + element.getIdentifier());
		getElementCache().put(element.getIdentifier(), element);
	}

//	default void invalidateElementCache() {
//		getElementCache().clear();
//		Stream.concat(Stream.concat(FeatureTreeMixin.super.getFeatures().stream(),
//						getConstraints().stream()),
//						FeatureModelTreeMixin.super.getFeatureModels().stream())
//				.forEach(this::invalidateElement);
//	}
//
//	default void invalidateFeatureCache() {
//		getFeatureCache().clear();
//		getFeatureCache().addAll(FeatureTreeMixin.super.getFeatures());
//	}
//
//	default void invalidateFeatureModelCache() {
//		getFeatureModelCache().clear();
//		getFeatureModelCache().addAll(FeatureModelTreeMixin.super.getFeatureModels());
//	}
//
//	default void invalidateCaches() {
//		Set<Feature> features = FeatureTreeMixin.super.getFeatures();
//		Set<FeatureModel> featureModels = FeatureModelTreeMixin.super.getFeatureModels();
//
//		getElementCache().clear();
//		Stream.concat(Stream.concat(features.stream(), getConstraints().stream()), featureModels.stream())
//				.forEach(this::invalidateElement);
//
//		getFeatureCache().clear();
//		getFeatureCache().addAll(features);
//
//		getFeatureModelCache().clear();
//		getFeatureModelCache().addAll(featureModels);
//	}

	@Override
	default Set<Feature> getFeatures() {
		return getFeatureCache();
	}

	@Override
	default Optional<Feature> getFeature(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		Element element = getElementCache().get(identifier);
		if (!(element instanceof Feature))
			return Optional.empty();
		return Optional.of((Feature) element);
	}

	@Override
	default Optional<Constraint> getConstraint(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		Element element = getElementCache().get(identifier);
		if (!(element instanceof Constraint))
			return Optional.empty();
		return Optional.of((Constraint) element);
	}

//	@Override
//	default Set<FeatureModel> getFeatureModels() {
//		return Trees.parallelStream(getFeatureModelTree()).map(FeatureModelTree::getFeatureModel).collect(Collectors.toSet());
//	}

	// todo: getfeaturemodel()

	interface Mutator extends FeatureModelFeatureTreeMixin.Mutator, FeatureModelConstraintMixin.Mutator {
		FeatureModel getFeatureModel();

//		@Override
//		default void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
//			FeatureTreeMixin.Mutator.super.addFeatureBelow(newFeature, parentFeature, index);
//			getFeatureModel().getFeatureCache().add(newFeature);
//			getFeatureModel().getElementCache().put(newFeature.getIdentifier(), newFeature);
//		}
//
//		@Override
//		default void removeFeature(Feature feature) {
//			FeatureTreeMixin.Mutator.super.removeFeature(feature);
//			getFeatureModel().getFeatureCache().remove(feature);
//			getFeatureModel().getElementCache().remove(feature.getIdentifier());
//		}
//
//		@Override
//		default void setConstraint(int index, Constraint constraint) {
//			getFeatureModel().getElementCache().remove(getFeatureModel().getConstraints().get(index).getIdentifier());
//			ConstraintMixin.Mutator.super.setConstraint(index, constraint);
//			getFeatureModel().getElementCache().put(constraint.getIdentifier(), constraint);
//		}
//
//		@Override
//		default void addConstraint(Constraint newConstraint, int index) {
//			ConstraintMixin.Mutator.super.addConstraint(newConstraint, index);
//			getFeatureModel().getElementCache().put(newConstraint.getIdentifier(), newConstraint);
//		}
//
//		@Override
//		default void removeConstraint(Constraint constraint) {
//			ConstraintMixin.Mutator.super.removeConstraint(constraint);
//			getFeatureModel().getElementCache().remove(constraint.getIdentifier());
//		}
//
//		@Override
//		default Constraint removeConstraint(int index) {
//			Constraint constraint = ConstraintMixin.Mutator.super.removeConstraint(index);
//			getFeatureModel().getElementCache().remove(constraint.getIdentifier());
//			return constraint;
//		}
	}
}
