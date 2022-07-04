package org.spldev.model;

import org.spldev.model.mixins.FeatureModelFeatureTreeMixin;
import org.spldev.model.util.Mutable;
import org.spldev.util.tree.Trees;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Orders features in different ways. By default, the feature-tree preorder is
 * used.
 *
 * @author Elias Kuiter
 */
public abstract class FeatureOrder implements Function<FeatureModelFeatureTreeMixin, List<Feature>>, Mutable<FeatureOrder, FeatureOrder.Mutator> {
	protected boolean isUserDefined;
	protected Mutator mutator;

	public boolean isUserDefined() {
		return isUserDefined;
	}

	@Override
	public FeatureOrder.Mutator getMutator() {
		return mutator == null ? (mutator = new FeatureOrder.Mutator()) : mutator;
	}

	@Override
	public void setMutator(FeatureOrder.Mutator mutator) {
		this.mutator = mutator;
	}

	public static FeatureOrder ofPreOrder() {
		return new FeatureOrder() {
			@Override
			public List<Feature> apply(FeatureModelFeatureTreeMixin featureModel) {
				return Trees.preOrderStream(featureModel.getFeatureTree())
						.map(FeatureTree::getFeature)
						.collect(Collectors.toList());
			}
		};
	}

	public static FeatureOrder ofComparator(Comparator<Feature> featureComparator) {
		return new FeatureOrder() {
			@Override
			public List<Feature> apply(FeatureModelFeatureTreeMixin featureModel) {
				return featureModel.getFeatures().stream()
						.sorted(featureComparator)
						.collect(Collectors.toList());
			}
		};
	}

	public static FeatureOrder ofList(List<Feature> featureList) { // todo: maybe make this list mutable for easier editing?
		return new FeatureOrder() {
			@Override
			public List<Feature> apply(FeatureModelFeatureTreeMixin featureModel) {
				return Stream.concat(featureList.stream().filter(featureModel.getFeatures()::contains),
								featureModel.getFeatures().stream().filter(feature -> !featureList.contains(feature)))
						.collect(Collectors.toList());
			}
		};
	}

	public class Mutator implements org.spldev.model.util.Mutator<FeatureOrder> {
		@Override
		public FeatureOrder getMutable() {
			return FeatureOrder.this;
		}

		public void setUserDefined(boolean userDefined) {
			FeatureOrder.this.isUserDefined = userDefined;
		}
	}
}
