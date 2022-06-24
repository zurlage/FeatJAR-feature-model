package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureTree;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.util.tree.Trees;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements a {@link FeatureModel} mixin for common operations on the
 * {@link FeatureTree}.
 *
 * @author Elias Kuiter
 */
public interface FeatureModelFeatureTreeMixin {
	FeatureTree getFeatureTree();

	default Set<Feature> getFeatures() {
		return Trees.preOrderStream(getFeatureTree()).map(FeatureTree::getFeature).collect(Collectors.toSet());
	}

	default int getNumberOfFeatures() {
		return getFeatures().size();
	}

	default Feature getRootFeature() {
		return getFeatureTree().getRoot().getFeature();
	}

	default Optional<Feature> getFeature(Identifier identifier) {
		Objects.requireNonNull(identifier);
		return getFeatures().stream().filter(feature -> feature.getIdentifier().equals(identifier)).findFirst();
	}

	default Set<Feature> getFeaturesByName(String name) {
		Objects.requireNonNull(name);
		return getFeatures().stream().filter(feature -> feature.getName().equals(name)).collect(Collectors.toSet());
	}

	default boolean hasFeature(Identifier identifier) {
		return getFeature(identifier).isPresent();
	}

	default boolean hasFeature(Feature feature) {
		return hasFeature(feature.getIdentifier());
	}

	interface Mutator extends org.spldev.featuremodel.util.Mutator<FeatureModel> {
		default void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
			Objects.requireNonNull(newFeature);
			Objects.requireNonNull(parentFeature);
			if (getMutable().hasFeature(newFeature) || !getMutable().hasFeature(parentFeature)) {
				throw new IllegalArgumentException();
			}
			parentFeature.getFeatureTree().addChild(index, newFeature.getFeatureTree());
		}

		default void addFeatureBelow(Feature newFeature, Feature parentFeature) {
			Objects.requireNonNull(newFeature);
			Objects.requireNonNull(parentFeature);
			addFeatureBelow(newFeature, parentFeature, parentFeature.getFeatureTree().getNumberOfChildren());
		}

		default void addFeatureNextTo(Feature newFeature, Feature siblingFeature) {
			Objects.requireNonNull(newFeature);
			Objects.requireNonNull(siblingFeature);
			if (!siblingFeature.getFeatureTree().hasParent() || !getMutable().hasFeature(siblingFeature)) {
				throw new IllegalArgumentException();
			}
			addFeatureBelow(newFeature,
				siblingFeature.getFeatureTree().getParent().get().getFeature(),
				siblingFeature.getFeatureTree().getIndex().get() + 1);
		}

		default Feature createFeatureBelow(Feature parentFeature, int index) {
			Feature newFeature = new Feature(getMutable());
			addFeatureBelow(newFeature, parentFeature, index);
			return newFeature;
		}

		default Feature createFeatureBelow(Feature parentFeature) {
			Feature newFeature = new Feature(getMutable());
			addFeatureBelow(newFeature, parentFeature);
			return newFeature;
		}

		default Feature createFeatureNextTo(Feature siblingFeature) {
			Feature newFeature = new Feature(getMutable());
			addFeatureNextTo(newFeature, siblingFeature);
			return newFeature;
		}

		default void removeFeature(Feature feature) { // todo what about the containing constraints?
			Objects.requireNonNull(feature);
			if (feature.equals(getMutable().getRootFeature()) || !getMutable().hasFeature(feature)) {
				throw new IllegalArgumentException();
			}

			final FeatureTree parentFeatureTree = feature.getFeatureTree().getParent().get();

			if (parentFeatureTree.getNumberOfChildren() == 1) {
				parentFeatureTree.mutate(mutator -> {
					if (feature.getFeatureTree().isAnd()) {
						mutator.setAnd();
					} else if (feature.getFeatureTree().isAlternative()) {
						mutator.setAlternative();
					} else {
						mutator.setOr();
					}
				});
			}

			final int index = feature.getFeatureTree().getIndex().get();
			while (feature.getFeatureTree().hasChildren()) {
				parentFeatureTree.addChild(
					index,
					feature.getFeatureTree().removeChild(feature.getFeatureTree().getNumberOfChildren() - 1));
			}

			parentFeatureTree.removeChild(feature.getFeatureTree());
		}
	}

	interface Analyzer extends org.spldev.featuremodel.util.Analyzer<FeatureModel> {
		default Set<Feature> getCoreFeatures() {
			return Collections.emptySet();
		}

		default Set<Feature> getDeadFeatures() {
			return Collections.emptySet(); // use extensions
		}

		// ...
	}
}
