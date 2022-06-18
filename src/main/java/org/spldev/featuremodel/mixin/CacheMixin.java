package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.*;
import org.spldev.util.tree.Trees;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Feature model
 * assumes that features/constraints are only added/deleted through the feature model class, not manually
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public interface CacheMixin extends FeatureTreeMixin, ConstraintMixin, FeatureModelTreeMixin {
	Map<Identifier<?>, Element> getElementCache();
	Set<Feature> getFeatureCache();
	Set<FeatureModel> getFeatureModelCache();

//	public FeatureModel(Identifier.Factory<?> identifierFactory) {
//		super(identifierFactory);
//		refreshElementCache();
//		refreshFeatureCache();
//	}

	default void invalidateElementCache() {
		getElementCache().clear();
		Stream.concat(Stream.concat(FeatureTreeMixin.super.getFeatures().stream(),
						getConstraints().stream()),
						FeatureModelTreeMixin.super.getFeatureModels().stream())
				.forEach(element -> getElementCache().put(element.getIdentifier(), element));
	}

	default void invalidateFeatureCache() {
		getFeatureCache().clear();
		getFeatureCache().addAll(FeatureTreeMixin.super.getFeatures());
	}

	default void invalidateFeatureModelCache() {
		getFeatureModelCache().clear();
		getFeatureModelCache().addAll(FeatureModelTreeMixin.super.getFeatureModels());
	}

	default void invalidateCaches() {
		Set<Feature> features = FeatureTreeMixin.super.getFeatures();
		Set<FeatureModel> featureModels = FeatureModelTreeMixin.super.getFeatureModels();

		getElementCache().clear();
		Stream.concat(Stream.concat(features.stream(), getConstraints().stream()), featureModels.stream())
				.forEach(element -> getElementCache().put(element.getIdentifier(), element));

		getFeatureCache().clear();
		getFeatureCache().addAll(features);

		getFeatureModelCache().clear();
		getFeatureModelCache().addAll(featureModels);
	}

	default void manipulateUnsafe(Runnable r) {
		try {
			r.run();
		} finally {
			invalidateCaches();
		}
	}

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
	default void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
		FeatureTreeMixin.super.addFeatureBelow(newFeature, parentFeature, index);
		getFeatureCache().add(newFeature);
		getElementCache().put(newFeature.getIdentifier(), newFeature);
	}

	@Override
	default void removeFeature(Feature feature) {
		FeatureTreeMixin.super.removeFeature(feature);
		getFeatureCache().remove(feature);
		getElementCache().remove(feature.getIdentifier());
	}

	@Override
	default Optional<Constraint> getConstraint(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		Element element = getElementCache().get(identifier);
		if (!(element instanceof Constraint))
			return Optional.empty();
		return Optional.of((Constraint) element);
	}

	@Override
	default void setConstraint(int index, Constraint constraint) {
		getElementCache().remove(getConstraints().get(index).getIdentifier());
		ConstraintMixin.super.setConstraint(index, constraint);
		getElementCache().put(constraint.getIdentifier(), constraint);
	}

	@Override
	default void addConstraint(Constraint newConstraint, int index) {
		ConstraintMixin.super.addConstraint(newConstraint, index);
		getElementCache().put(newConstraint.getIdentifier(), newConstraint);
	}

	@Override
	default void removeConstraint(Constraint constraint) {
		ConstraintMixin.super.removeConstraint(constraint);
		getElementCache().remove(constraint.getIdentifier());
	}

	@Override
	default Constraint removeConstraint(int index) {
		Constraint constraint = ConstraintMixin.super.removeConstraint(index);
		getElementCache().remove(constraint.getIdentifier());
		return constraint;
	}

	@Override
	default Set<FeatureModel> getFeatureModels() {
		return Trees.parallelStream(getFeatureModelTree()).map(FeatureModelTree::getFeatureModel).collect(Collectors.toSet());
	}
}
