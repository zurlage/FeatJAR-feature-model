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

import org.prop4j.Node;

/**
 * Factory to create or copy instances of {@link Feature}, {@link FeatureModel}, {@link Constraint}, and to obfuscate {@link FeatureModel}s.
 *
 * @author Sebastian Krieter
 * @author Rahel Arens
 * @author Benedikt Jutz
 */
public interface IFeatureModelFactory extends IFactory<FeatureModel> {

	public static String extensionPointID = "FMFactory";

	public static String extensionID = "fmFactory";

	Constraint createConstraint(FeatureModel featureModel, Node propNode);

	Feature createFeature(FeatureModel featureModel, String name);

	FeatureModel createObfuscatedFeatureModel(FeatureModel featureModel, String salt);

	Feature copyFeature(FeatureModel featureModel, Feature oldFeature);

	Constraint copyConstraint(FeatureModel featureModel, Constraint oldConstraint);

}
