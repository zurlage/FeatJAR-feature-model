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
package org.spldev.featuremodel.io;

import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureModelFactory;
import org.spldev.util.io.format.Format;

/**
 * Abstract class for foramts that handle feature models.
 *
 * @author Sebastian Krieter
 */
public abstract class FeatureModelFormat implements Format<FeatureModel>, IFeatureNameValidator {

	protected FeatureModelFactory factory = FeatureModelFactory.getInstance();
	protected IFeatureNameValidator validator;

	public FeatureModelFormat() {
	}

	protected FeatureModelFormat(FeatureModelFormat oldFormat) {
		factory = oldFormat.factory;
		validator = oldFormat.validator;
	}

	public void setFeatureNameValidator(IFeatureNameValidator validator) {
		this.validator = validator;
	}

	public IFeatureNameValidator getFeatureNameValidator() {
		return validator;
	}

	@Override
	public boolean isValidFeatureName(String featureName) {
		return true;
	}
}
