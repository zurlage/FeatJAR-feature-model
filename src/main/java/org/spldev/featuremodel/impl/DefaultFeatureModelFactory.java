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

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.PluginID;
import org.spldev.featuremodel.Constraint;
import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.editing.FeatureModelObfuscator;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;

/**
 *
 * @author Sebastian Krieter
 */
public class DefaultFeatureModelFactory implements IFeatureModelFactory {

	public static final String ID = PluginID.PLUGIN_ID + ".DefaultFeatureModelFactory";

	public static DefaultFeatureModelFactory getInstance() {
		return new DefaultFeatureModelFactory();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean initExtension() {
		return true;
	}

	@Override
	public org.spldev.featuremodel.impl.Constraint createConstraint(FeatureModel featureModel, Node propNode) {
		return new org.spldev.featuremodel.impl.Constraint(featureModel, propNode);
	}

	@Override
	public org.spldev.featuremodel.impl.Feature createFeature(FeatureModel featureModel, String name) {
		return new org.spldev.featuremodel.impl.Feature(featureModel, name);
	}

	@Override
	public org.spldev.featuremodel.impl.FeatureModel create() {
		return new org.spldev.featuremodel.impl.FeatureModel(ID);
	}

	@Override
	public Feature copyFeature(FeatureModel featureModel, Feature oldFeature) {
		return oldFeature.getStructure().clone(featureModel).getFeature();
	}

	@Override
	public Constraint copyConstraint(FeatureModel featureModel, Constraint oldConstraint) {
		return oldConstraint.clone(featureModel);
	}

	@Override
	public FeatureModel createObfuscatedFeatureModel(FeatureModel featureModel, String salt) {
		return LongRunningWrapper.runMethod(new FeatureModelObfuscator(featureModel, salt));
	}

}
