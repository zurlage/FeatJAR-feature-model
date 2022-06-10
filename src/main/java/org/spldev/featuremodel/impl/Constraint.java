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
package org.spldev.featuremodel.impl;

import org.spldev.featuremodel.IConstraint;
import org.spldev.featuremodel.IFeatureModel;
import org.spldev.formula.structure.Formula;

/**
 * Represents a propositional constraint below the feature diagram.
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 */
public class Constraint extends AConstraint {

	protected Constraint(Constraint oldConstraint, IFeatureModel featureModel) {
		super(oldConstraint, featureModel);
	}

	public Constraint(IFeatureModel featureModel, Formula propNode) {
		super(featureModel, propNode);
	}

	@Override
	public IConstraint clone(IFeatureModel newFeatureModel) {
		return new Constraint(this, newFeatureModel);
	}

}
