/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.spldev.featuremodel;

import org.spldev.event.FeatureIDEEvent;
import org.spldev.util.tree.structure.RootedTree;
import org.spldev.util.tree.structure.Tree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Manages all structural information of a feature.<br>
 * Intended for tree structures (features are represented by tree nodes).
 *
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 */
public class FeatureTree extends RootedTree<FeatureTree> {
	/**
	 * Feature at the root of this feature tree.
	 */
	protected final Feature feature;

	/**
	 * Whether this tree's feature is mandatory or optional.
	 */
	protected boolean isMandatory;

	/**
	 * Whether this feature tree models an AND group of features.
	 */
	protected boolean isAnd;

	/**
	 * Whether it is possible to choose multiple child features.
	 */
	protected boolean isMultiple;

	protected boolean isConcrete; // todo - move?

	protected boolean isHidden; // todo - move?

	protected List<Constraint> partOfConstraints = new LinkedList<>();

	protected FeatureTree(FeatureTree oldStructure, FeatureModel newFeatureModel) {
		this(oldStructure, newFeatureModel, false);
	}

	protected FeatureTree(FeatureTree oldStructure, FeatureModel newFeatureModel, boolean copySubtree) {
		if (newFeatureModel != null) {
			feature = oldStructure.feature.clone(newFeatureModel, this);
			if (copySubtree) {
				newFeatureModel.addFeature(feature);
			}
		} else {
			feature = oldStructure.feature;
		}

		isMandatory = oldStructure.isMandatory;
		isConcrete = oldStructure.isConcrete;
		isAnd = oldStructure.isAnd;
		isMultiple = oldStructure.isMultiple;
		isHidden = oldStructure.isHidden;

		if (copySubtree) {
			for (final FeatureTree child : oldStructure.children) {
				addChild(child.cloneSubtree(newFeatureModel));
			}
		}
	}

	public FeatureTree(Feature feature) {
		this.feature = feature;

		isMandatory = false;
		isConcrete = true;
		isAnd = true;
		isMultiple = false;
		isHidden = false;
	}

	@Override
	public void addChild(FeatureTree newChild) {
		super.addChild(newChild);
		fireChildrenChanged();
	}

	@Override
	public void addChild(int index, FeatureTree newChild) {
		super.addChild(index, newChild);
		fireChildrenChanged();
	}

	@Override
	public void removeChild(FeatureTree child) {
		super.removeChild(child);
		fireChildrenChanged();
	}

	@Override
	public FeatureTree removeChild(int index) {
		FeatureTree child = super.removeChild(index);
		fireChildrenChanged();
		return child;
	}

	@Override
	public void replaceChild(FeatureTree oldChild, FeatureTree newChild) {
		super.replaceChild(oldChild, newChild);
		fireChildrenChanged();
	}

	public void changeToAlternative() {
		if (getNumberOfChildren() <= 1) {
			return;
		}
		isAnd = false;
		isMultiple = false;
		fireChildrenChanged();
	}

	public void changeToAnd() {
		isAnd = true;
		isMultiple = false;
		fireChildrenChanged();
	}

	public void changeToOr() {
		if (getNumberOfChildren() <= 1) {
			return;
		}
		isAnd = false;
		isMultiple = true;
		fireChildrenChanged();
	}

	public FeatureTree cloneSubtree(FeatureModel newFeatureModel) {
		return new FeatureTree(this, newFeatureModel, true);
	}

	public FeatureTree clone(FeatureModel newFeatureModel) {
		return new FeatureTree(this, newFeatureModel, false);
	}

	protected void fireAttributeChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.ATTRIBUTE_CHANGED);
		feature.fireEvent(event);
	}

	protected void fireChildrenChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.GROUP_TYPE_CHANGED,
			Boolean.FALSE, Boolean.TRUE);
		feature.fireEvent(event);
	}

	protected void fireHiddenChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.FEATURE_HIDDEN_CHANGED,
			Boolean.FALSE, Boolean.TRUE);
		feature.fireEvent(event);
	}

	protected void fireMandatoryChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.MANDATORY_CHANGED,
			Boolean.FALSE, Boolean.TRUE);
		feature.fireEvent(event);
	}

	public boolean hasVisibleChildren(boolean showHiddenFeature) {
		boolean check = false;
		for (final FeatureTree child : children) {
			if ((!child.hasHiddenParent() || showHiddenFeature)) {
				check = true;
			}
		}
		return check;
	}

	public Feature getFeature() {
		return feature;
	}

	public Collection<Constraint> getRelevantConstraints() {
		setRelevantConstraints();
		return partOfConstraints;
	}

	public boolean isRoot() {
		return !hasParent();
	}

	public boolean hasHiddenParent() {

		if (isHidden()) {
			return true;
		}
		if (isRoot()) {

			return false;
		}
		FeatureTree p = getParent().get();

		while (p.hasParent()) {
			if (p.isHidden()) {
				return true;
			}
			p = p.getParent().get();

		}

		return false;
	}

	/**
	 * Returns true if the rule can be writen in a format like 'Ab [Cd] Ef :: Gh'.
	 */
	public boolean hasInlineRule() {
		return (getNumberOfChildren() > 1) && isAnd && isMandatory() && !isMultiple;
	}

	public boolean isAbstract() {
		return !isConcrete();
	}

	public boolean isAlternative() {
		return !isAnd && !isMultiple && (getNumberOfChildren() > 1);
	}

	public boolean isAnd() {
		return isAnd || (getNumberOfChildren() <= 1);
	}

	public boolean isANDPossible() {
		if ((parent == null) || parent.isAnd()) {
			return false;
		}
		for (final FeatureTree child : children) {
			if (child.isAnd()) {
				return false;
			}
		}
		return true;
	}

	public boolean isConcrete() {
		return isConcrete;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public boolean isMandatory() {
		return (parent == null) || (!parent.isAndInternal() && (parent.getNumberOfChildren() == 1)) || isMandatory;
	}

	public boolean isMandatorySet() {
		return isMandatory;
	}

	public boolean isMultiple() {
		return isMultiple && (getNumberOfChildren() > 1);
	}

	public boolean isOr() {
		return !isAnd && isMultiple && (getNumberOfChildren() > 1);
	}

	/**
	 * Returns the internal value of the variable {@code and}. In most cases the
	 * method {@link #isAnd()} should be used instead.
	 *
	 * @return the internal value of {@code and}
	 */
	public boolean isAndInternal() {
		return isAnd;
	}

	/**
	 * Returns the internal value of the variable {@code multiple}. In most cases
	 * the method {@link #isMultiple()} should be used instead.
	 *
	 * @return the internal value of {@code multiple}
	 */
	public boolean isMultipleInternal() {
		return isMultiple;
	}

	public void setAbstract(boolean value) {
		isConcrete = !value;
		fireAttributeChanged();
	}

	public void setAlternative() {
		isAnd = false;
		isMultiple = false;
	}

	public void setAnd() {
		isAnd = true;
	}

	public void setAND(boolean and) {
		this.isAnd = and;
		fireChildrenChanged();
	}

	@Override
	public Tree<FeatureTree> cloneNode() {
		return new FeatureTree(this, null, false); // todo ?
	}

	@Override
	public boolean equalsNode(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FeatureTree that = (FeatureTree) o;
		return isMandatory == that.isMandatory && isAnd == that.isAnd && isMultiple == that.isMultiple &&
			isConcrete == that.isConcrete && isHidden == that.isHidden && Objects.equals(feature, that.feature);
	}

	@Override
	public void setChildren(List<? extends FeatureTree> children) {
		super.setChildren(children);
		fireChildrenChanged();
	}

	public void setHidden(boolean hid) {
		isHidden = hid;
		fireHiddenChanged();
	}

	public void setMandatory(boolean mandatory) {
		this.isMandatory = mandatory;
		fireMandatoryChanged();
	}

	public void setMultiple(boolean multiple) {
		this.isMultiple = multiple;
		fireChildrenChanged();
	}

	public void setOr() {
		isAnd = false;
		isMultiple = true;
	}

	public void setRelevantConstraints() {
		final List<Constraint> constraintList = new LinkedList<>();
		for (final Constraint constraint : feature.getFeatureModel().getConstraints()) {
			for (final Feature f : constraint.getContainedFeatures()) {
				if (f.getName().equals(feature.getName())) {
					constraintList.add(constraint);
					break;
				}
			}
		}
		partOfConstraints = constraintList;
	}

	public void setRelevantConstraints(List<Constraint> constraints) {
		partOfConstraints = constraints;
	}
}
