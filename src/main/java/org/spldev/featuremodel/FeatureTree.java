package org.spldev.featuremodel;

import org.spldev.featuremodel.util.Mutable;
import org.spldev.util.tree.structure.RootedTree;
import org.spldev.util.tree.structure.Tree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An ordered {@link RootedTree} labeled with {@link Feature features}.
 * Implements elements of notation from feature-oriented domain analysis, such as mandatory/optional features and groups.
 *
 * @author Elias Kuiter
 */
public class FeatureTree extends RootedTree<FeatureTree> implements Mutable<FeatureTree, FeatureTree.Mutator> {
	/**
	 * Feature at the root of this feature tree.
	 */
	protected final Feature feature;

	/**
	 * Whether this tree's feature is mandatory or optional.
	 */
	protected boolean isMandatory = false;

	/**
	 * Minimum number of child features that may be selected.
	 */
	protected long groupMinimum = 0;

	/**
	 * Maximum number of child features that may be selected.
	 */
	protected long groupMaximum = Long.MAX_VALUE;

	protected Mutator mutator = null;

	public FeatureTree(Feature feature) {
		Objects.requireNonNull(feature);
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public boolean isMandatory() {
		return isMandatory; //todo
	}

	public long getGroupMinimum() {
		return groupMinimum;
	}

	public long getGroupMaximum() {
		return groupMaximum;
	}

	public boolean isAnd() {
		return groupMinimum == 0 && groupMaximum == Long.MAX_VALUE;
	}

	public boolean isAlternative() {
		return groupMinimum == 1 && groupMaximum == 1;
	}

	public boolean isOr() {
		return groupMinimum == 1 && groupMaximum == Long.MAX_VALUE;
	}

	public Set<Constraint> getContainingConstraints() {
		return feature.getFeatureModel().getConstraints().stream()
				.filter(constraint -> constraint.getContainedFeatures().stream()
						.anyMatch(feature::equals)).collect(Collectors.toSet());
	}

	@Override
	public Tree<FeatureTree> cloneNode() {
		throw new RuntimeException();
	}

	@Override
	public Mutator getMutator() {
		return mutator == null ? (mutator = new Mutator()) : mutator;
	}

	@Override
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}

	@Override
	public void invalidate() {
	}

	// todo hashcode, equals, tostring, clone

	public class Mutator implements org.spldev.featuremodel.util.Mutator<FeatureTree> {
		@Override
		public FeatureTree getMutable() {
			return FeatureTree.this;
		}

		public void setMandatory(boolean mandatory) {
			FeatureTree.this.isMandatory = mandatory;
		}

		public void setGroupMinimum(long groupMinimum) {
			FeatureTree.this.groupMinimum = groupMinimum;
		}

		public void setGroupMaximum(long groupMaximum) {
			FeatureTree.this.groupMaximum = groupMaximum;
		}

		public void setAnd() {
			groupMinimum = 0;
			groupMaximum = Long.MAX_VALUE;
		}

		public void setAlternative() {
			groupMinimum = 1;
			groupMaximum = 1;
		}

		public void setOr() {
			groupMinimum = 1;
			groupMaximum = Long.MAX_VALUE;
		}

		//todo addfeature... methods?
	}
}
