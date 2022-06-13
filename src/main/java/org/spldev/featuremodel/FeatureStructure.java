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

import org.spldev.featuremodel.event.FeatureIDEEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Manages all structural information of a feature.<br> Intended for tree structures (features are represented by tree nodes).
 *
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 */
public class FeatureStructure {
	protected boolean and;

	protected final LinkedList<FeatureStructure> children = new LinkedList<>();
	protected boolean concrete;
	protected final Feature correspondingFeature;

	protected boolean hidden;

	protected boolean mandatory;
	protected boolean multiple;

	protected FeatureStructure parent = null;
	protected List<Constraint> partOfConstraints = new LinkedList<>();

	protected FeatureStructure(FeatureStructure oldStructure, FeatureModel newFeatureModel) {
		this(oldStructure, newFeatureModel, false);
	}

	protected FeatureStructure(FeatureStructure oldStructure, FeatureModel newFeatureModel, boolean copySubtree) {
		if (newFeatureModel != null) {
			correspondingFeature = oldStructure.correspondingFeature.clone(newFeatureModel, this);
			if (copySubtree) {
				newFeatureModel.addFeature(correspondingFeature);
			}
		} else {
			correspondingFeature = oldStructure.correspondingFeature;
		}

		mandatory = oldStructure.mandatory;
		concrete = oldStructure.concrete;
		and = oldStructure.and;
		multiple = oldStructure.multiple;
		hidden = oldStructure.hidden;

		if (copySubtree) {
			for (final FeatureStructure child : oldStructure.children) {
				addNewChild(child.cloneSubtree(newFeatureModel));
			}
		}
	}

	public FeatureStructure(Feature correspondingFeature) {
		this.correspondingFeature = correspondingFeature;

		mandatory = false;
		concrete = true;
		and = true;
		multiple = false;
		hidden = false;
	}

	public void addChild(FeatureStructure newChild) {
		addNewChild(newChild);
		fireChildrenChanged();
	}

	public void addChildAtPosition(int index, FeatureStructure newChild) {
		if (index > children.size()) {
			children.add(newChild);
		} else {
			children.add(index, newChild);
		}
		newChild.setParent(this);
	}

	protected void addNewChild(FeatureStructure newChild) {
		children.add(newChild);
		newChild.setParent(this);
	}

	public void changeToAlternative() {
		if (getChildrenCount() <= 1) {
			return;
		}
		and = false;
		multiple = false;
		fireChildrenChanged();
	}

	public void changeToAnd() {
		and = true;
		multiple = false;
		fireChildrenChanged();
	}

	public void changeToOr() {
		if (getChildrenCount() <= 1) {
			return;
		}
		and = false;
		multiple = true;
		fireChildrenChanged();
	}

	public FeatureStructure cloneSubtree(FeatureModel newFeatureModel) {
		return new FeatureStructure(this, newFeatureModel, true);
	}

	public FeatureStructure clone(FeatureModel newFeatureModel) {
		return new FeatureStructure(this, newFeatureModel, false);
	}

	protected void fireAttributeChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.ATTRIBUTE_CHANGED);
		correspondingFeature.fireEvent(event);
	}

	protected void fireChildrenChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.GROUP_TYPE_CHANGED, Boolean.FALSE, Boolean.TRUE);
		correspondingFeature.fireEvent(event);
	}

	protected void fireHiddenChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.FEATURE_HIDDEN_CHANGED, Boolean.FALSE, Boolean.TRUE);
		correspondingFeature.fireEvent(event);
	}

	protected void fireMandatoryChanged() {
		final FeatureIDEEvent event = new FeatureIDEEvent(this, FeatureIDEEvent.EventType.MANDATORY_CHANGED, Boolean.FALSE, Boolean.TRUE);
		correspondingFeature.fireEvent(event);
	}

	public int getChildIndex(FeatureStructure feature) {
		return children.indexOf(feature);
	}

	public List<FeatureStructure> getChildren() {
		return children;
	}

	public boolean hasVisibleChildren(boolean showHiddenFeature) {
		boolean check = false;
		for (final FeatureStructure child : children) {
			if ((!child.hasHiddenParent() || showHiddenFeature)) {
				check = true;
			}
		}
		return check;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public Feature getFeature() {
		return correspondingFeature;
	}

	public FeatureStructure getFirstChild() {
		if (children.isEmpty()) {
			return null;
		}
		return children.get(0);
	}

	public FeatureStructure getLastChild() {
		if (!children.isEmpty()) {
			return children.getLast();
		}
		return null;
	}

	public FeatureStructure getParent() {
		return parent;
	}

	public Collection<Constraint> getRelevantConstraints() {
		setRelevantConstraints();
		return partOfConstraints;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public boolean hasHiddenParent() {

		if (isHidden()) {
			return true;
		}
		if (isRoot()) {

			return false;
		}
		FeatureStructure p = getParent();

		while (!p.isRoot()) {
			if (p.isHidden()) {
				return true;
			}
			p = p.getParent();

		}

		return false;
	}

	/**
	 * Returns true if the rule can be writen in a format like 'Ab [Cd] Ef :: Gh'.
	 */
	public boolean hasInlineRule() {
		return (getChildrenCount() > 1) && and && isMandatory() && !multiple;
	}

	public boolean isAbstract() {
		return !isConcrete();
	}

	public boolean isAlternative() {
		return !and && !multiple && (getChildrenCount() > 1);
	}

	public boolean isAncestor(FeatureStructure parent) {
		FeatureStructure currParent = getParent();
		while (currParent != null) {
			if (parent == currParent) {
				return true;
			}
			currParent = currParent.getParent();
		}
		return false;
	}

	public boolean isAnd() {
		return and || (getChildrenCount() <= 1);
	}

	public boolean isANDPossible() {
		if ((parent == null) || parent.isAnd()) {
			return false;
		}
		for (final FeatureStructure child : children) {
			if (child.isAnd()) {
				return false;
			}
		}
		return true;
	}

	public boolean isConcrete() {
		return concrete;
	}

	public boolean isFirstChild(FeatureStructure child) {
		return children.indexOf(child) == 0;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isMandatory() {
		return (parent == null) || (!parent.isAndInternal() && (parent.getChildrenCount() == 1)) || mandatory;
	}

	public boolean isMandatorySet() {
		return mandatory;
	}

	public boolean isMultiple() {
		return multiple && (getChildrenCount() > 1);
	}

	public boolean isOr() {
		return !and && multiple && (getChildrenCount() > 1);
	}

	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Returns the internal value of the variable {@code and}. In most cases the method {@link #isAnd()} should be used instead.
	 *
	 * @return the internal value of {@code and}
	 */
	public boolean isAndInternal() {
		return and;
	}

	/**
	 * Returns the internal value of the variable {@code multiple}. In most cases the method {@link #isMultiple()} should be used instead.
	 *
	 * @return the internal value of {@code multiple}
	 */
	public boolean isMultipleInternal() {
		return multiple;
	}

	public void removeChild(FeatureStructure child) {
		if (!children.remove(child)) {
			throw new NoSuchElementException();
		}
		child.setParent(null);
		fireChildrenChanged();
	}

	public FeatureStructure removeLastChild() {
		final FeatureStructure child = children.removeLast();
		child.setParent(null);
		fireChildrenChanged();
		return child;
	}

	public void replaceChild(FeatureStructure oldChild, FeatureStructure newChild) {
		final int index = children.indexOf(oldChild);
		children.set(index, newChild);
		oldChild.setParent(null);
		newChild.setParent(this);
		fireChildrenChanged();
	}

	public void setAbstract(boolean value) {
		concrete = !value;
		fireAttributeChanged();
	}

	public void setAlternative() {
		and = false;
		multiple = false;
	}

	public void setAnd() {
		and = true;
	}

	public void setAND(boolean and) {
		this.and = and;
		fireChildrenChanged();
	}

	public void setChildren(List<FeatureStructure> children) {
		this.children.clear();
		for (final FeatureStructure child : children) {
			addNewChild(child);
		}
		fireChildrenChanged();
	}

	public void setHidden(boolean hid) {
		hidden = hid;
		fireHiddenChanged();
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
		fireMandatoryChanged();
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
		fireChildrenChanged();
	}

	public void setOr() {
		and = false;
		multiple = true;
	}

	public void setParent(FeatureStructure newParent) {
		if (newParent == parent) {
			return;
		}
		parent = newParent;
	}

	public void setRelevantConstraints() {
		final List<Constraint> constraintList = new LinkedList<>();
		for (final Constraint constraint : correspondingFeature.getFeatureModel().getConstraints()) {
			for (final Feature f : constraint.getContainedFeatures()) {
				if (f.getName().equals(correspondingFeature.getName())) {
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

	public String toString() {
		final StringBuilder sb = new StringBuilder("FeatureStructure=(");
		FeatureUtils.print(getFeature(), sb);
		sb.append(")");
		return sb.toString();
	}
}
