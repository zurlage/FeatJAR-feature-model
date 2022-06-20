package org.spldev.featuremodel;

import org.spldev.util.tree.structure.RootedTree;
import org.spldev.util.tree.structure.Tree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Feature tree
 *
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class FeatureTree extends RootedTree<FeatureTree> {
	/**
	 * Feature at the root of this feature tree.
	 */
	protected final Feature feature;

	protected final FeatureModel featureModel;

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

	protected Set<Constraint> containingConstraintsCache = new HashSet<>();

	public FeatureTree(Feature feature, FeatureModel featureModel) {
		Objects.requireNonNull(feature);
		Objects.requireNonNull(featureModel);
		this.feature = feature;
		this.featureModel = featureModel;
	}

	public Feature getFeature() {
		return feature;
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public boolean isMandatory() {
		return isMandatory; //todo
	}

	public void setMandatory(boolean mandatory) {
		this.isMandatory = mandatory;
	}

	public long getGroupMinimum() {
		return groupMinimum;
	}

	public void setGroupMinimum(long groupMinimum) {
		this.groupMinimum = groupMinimum;
	}

	public long getGroupMaximum() {
		return groupMaximum;
	}

	public void setGroupMaximum(long groupMaximum) {
		this.groupMaximum = groupMaximum;
	}

	public boolean isAnd() {
		return groupMinimum == 0 && groupMaximum == Long.MAX_VALUE;
	}

	public void setAnd() {
		groupMinimum = 0;
		groupMaximum = Long.MAX_VALUE;
	}

	public boolean isAlternative() {
		return groupMinimum == 1 && groupMaximum == 1;
	}

	public void setAlternative() {
		groupMinimum = 1;
		groupMaximum = 1;
	}

	public boolean isOr() {
		return groupMinimum == 1 && groupMaximum == Long.MAX_VALUE;
	}

	public void setOr() {
		groupMinimum = 1;
		groupMaximum = Long.MAX_VALUE;
	}

	public Set<Constraint> getRelevantConstraints() {
		refreshContainingConstraints();
		return containingConstraintsCache;
	}

	public void refreshContainingConstraints() {
		containingConstraintsCache = featureModel.getConstraints().stream()
				.filter(constraint -> constraint.getContainedFeatures().stream()
						.anyMatch(feature::equals)).collect(Collectors.toSet());
	}

	@Override
	public Tree<FeatureTree> cloneNode() {
		throw new RuntimeException();
	}
}
