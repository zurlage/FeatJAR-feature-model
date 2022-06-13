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

/**
 * Manages all additional properties of a feature.
 *
 * @author Sebastian Krieter
 */
public class FeatureProperty {
	protected final Feature correspondingFeature;

	protected String description;
	protected boolean implicit;

	public FeatureProperty(FeatureProperty oldProperty, Feature correspondingFeature) {
		this.correspondingFeature = correspondingFeature != null ? correspondingFeature : oldProperty.correspondingFeature;
		description = oldProperty.description.toString();
		implicit = oldProperty.implicit;
	}

	public FeatureProperty(Feature correspondingFeature) {
		this.correspondingFeature = correspondingFeature;
		description = "";
		implicit = false;
	}

	public FeatureProperty clone(Feature newFeature) {
		return new FeatureProperty(this, newFeature);
	}

	/**
	 *
	 * @return The description of the Feature.
	 */
	public String getDescription() {
		return description;
	}

	@Deprecated
	public String getDisplayName() {
		return correspondingFeature.getName();
	}

	public Feature getFeature() {
		return correspondingFeature;
	}

	public void setDescription(final CharSequence description) {
		this.description = description.toString();
	}

	@Deprecated
	public void setDisplayName(CharSequence name) {}

	public boolean isConstraintSelected() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public boolean selectConstraint(boolean state) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Implicit features can be used to represent features that exist only for technical reasons, such as the implicit root feature of UVL models with multiple
	 * actual root features.
	 *
	 * @return Whether the corresponding feature is implicit.
	 */
	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}
}
