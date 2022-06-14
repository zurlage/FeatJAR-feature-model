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

import java.util.*;

/**
 * Manages all structural information of a feature model. Intended for tree
 * structures (features are represented by tree nodes).
 *
 * @author Sebastian Krieter
 */
public class FeatureModelStructure {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((rootFeature == null) ? 0 : rootFeature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FeatureModelStructure other = (FeatureModelStructure) obj;
		if (correspondingFeatureModel == null) {
			if (other.correspondingFeatureModel != null) {
				return false;
			}
		}
		// else if (!correspondingFeatureModel.equals(other.correspondingFeatureModel))
		// return false;
		if (rootFeature == null) {
			if (other.rootFeature != null) {
				return false;
			}
		} else if (!rootFeature.equals(other.rootFeature)) {
			return false;
		}
		return true;
	}

	protected final FeatureModel correspondingFeatureModel;

	protected FeatureTree rootFeature;

	protected boolean showHiddenFeatures = false;

	protected FeatureModelStructure(FeatureModelStructure oldStructure, FeatureModel correspondingFeatureModel) {
		this.correspondingFeatureModel = correspondingFeatureModel != null ? correspondingFeatureModel
			: oldStructure.correspondingFeatureModel;

		rootFeature = oldStructure.rootFeature;
	}

	public FeatureModelStructure(FeatureModel correspondingFeatureModel) {
		this.correspondingFeatureModel = correspondingFeatureModel;
	}

	public FeatureModelStructure clone(FeatureModel newFeatureNodel) {
		return new FeatureModelStructure(this, newFeatureNodel);
	}

	public FeatureModel getFeatureModel() {
		return correspondingFeatureModel;
	}

	public Collection<Feature> getFeaturesPreorder() {
		final List<Feature> preorderFeatures = new LinkedList<>();
		if (rootFeature != null) {
			getFeaturesPreorder(rootFeature, preorderFeatures);
		}
		return Collections.unmodifiableCollection(preorderFeatures);
	}

	protected void getFeaturesPreorder(FeatureTree featureTree, List<Feature> preorderFeatures) {
		preorderFeatures.add(featureTree.getFeature());
		for (final FeatureTree child : featureTree.getChildren()) {
			getFeaturesPreorder(child, preorderFeatures);
		}
	}

	public FeatureTree getRoot() {
		return rootFeature;
	}

	public boolean hasAbstract() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if (f.getStructure().isAbstract()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAlternativeGroup() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if ((f.getStructure().getNumberOfChildren() > 1) && f.getStructure().isAlternative()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAndGroup() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if ((f.getStructure().getNumberOfChildren() > 1) && f.getStructure().isAnd()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasConcrete() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if (f.getStructure().isConcrete()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasHidden() {
		for (final Feature f : correspondingFeatureModel.getFeatures()) {
			if (f.getStructure().isHidden()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMandatoryFeatures() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			final Optional<FeatureTree> parent = f.getStructure().getParent();
			if (parent.isPresent() && parent.get().isAnd() && f.getStructure().isMandatory()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasOptionalFeatures() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if (!f.equals(rootFeature.getFeature()) && f.getStructure().getParent().isPresent() && f.getStructure()
				.getParent().get().isAnd()
				&& !f.getStructure().isMandatory()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasOrGroup() {
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if ((f.getStructure().getNumberOfChildren() > 1) && f.getStructure().isOr()) {
				return true;
			}
		}
		return false;
	}

	public int numAlternativeGroup() {
		int count = 0;
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if ((f.getStructure().getNumberOfChildren() > 1) && f.getStructure().isAlternative()) {
				count++;
			}
		}
		return count;
	}

	public int numOrGroup() {
		int count = 0;
		for (final Feature f : correspondingFeatureModel.getVisibleFeatures()) {
			if ((f.getStructure().getNumberOfChildren() > 1) && f.getStructure().isOr()) {
				count++;
			}
		}
		return count;
	}

	public void replaceRoot(FeatureTree feature) {
		// TODO remove all features that are no children of the new root (part of a
		// different sub tree)
		correspondingFeatureModel.deleteFeatureFromTable(rootFeature.getFeature());

		feature.setParent(null);
		rootFeature = feature;
	}

	public void setRoot(FeatureTree root) {
		rootFeature = root;
	}
}
