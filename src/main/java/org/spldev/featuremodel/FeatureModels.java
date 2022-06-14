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

import org.spldev.formula.structure.Formula;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Several convenience methods for handling feature models, features and
 * constraints.
 *
 * @author Marcus Pinnecke
 */
public final class FeatureModels {
	public static final Function<Feature, String> GET_OLD_FEATURE_NAME = feature -> feature.getFeatureModel()
		.getRenamingsManager().getOldName(feature.getName());

	public static final void addAnnotation(FeatureModel featureModel, CharSequence annotation) {

		featureModel.getProperty().addAnnotation(annotation);
	}

	public static final void addChild(Feature feature, Feature newChild) {

		feature.getStructure().addChild(newChild.getStructure());
	}

	public static final void addChild(Feature feature, int index, Feature newChild) {

		feature.getStructure().addChild(index, newChild.getStructure());
	}

	public static final void addComment(FeatureModel featureModel, CharSequence comment) {

		featureModel.getProperty().addComment(comment);
	}

	public static final void addConstraint(FeatureModel featureModel, Constraint constraint) {

		featureModel.addConstraint(constraint);
	}

	public static final void addConstraint(FeatureModel featureModel, Constraint constraint, int index) {

		featureModel.addConstraint(constraint, index);
	}

	public static final boolean addFeature(FeatureModel featureModel, Feature feature) {

		return featureModel.addFeature(feature);
	}

	public static final void addConstraint(FeatureModel featureModel, Formula node) {

		featureModel.addConstraint(new Constraint(featureModel, node));
	}

	public static final void addConstraint(FeatureModel featureModel, Formula node, int index) {

		featureModel.addConstraint(new Constraint(featureModel, node), index);
	}

	public static final void changeToAlternative(Feature feature) {

		feature.getStructure().changeToAlternative();
	}

	public static final void changeToAnd(Feature feature) {

		feature.getStructure().changeToAnd();
	}

	public static final void changeToOr(Feature feature) {

		feature.getStructure().changeToOr();
	}

	public static final Feature clone(Feature feature) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public static final Feature clone(Feature feature, FeatureModel featureModel, boolean complete) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public static final FeatureModel clone(FeatureModel featureModel) {

		return featureModel.clone();
	}

	public static final void createDefaultValues(FeatureModel featureModel, CharSequence projectName) {

		featureModel.createDefaultValues(projectName);
	}

	public static final boolean deleteFeature(FeatureModel featureModel, Feature feature) {

		return featureModel.deleteFeature(feature);
	}

	public static final void deleteFeatureFromTable(FeatureModel featureModel, Feature feature) {

		featureModel.deleteFeatureFromTable(feature);
	}

	public static final void reset(FeatureModel featureModel) {

		featureModel.reset();
	}

	public static final void setAbstract(Feature feature, boolean value) {

		feature.getStructure().setAbstract(value);
	}

	public static final void setAlternative(Feature feature) {

		feature.getStructure().setAlternative();
	}

	public static final void setAnd(Feature feature) {

		feature.getStructure().setAnd();
	}

	public static void setAnd(Feature feature, boolean and) {

		feature.getStructure().setAND(and);
	}

	public static final void setAND(Feature feature, boolean and) {

		feature.getStructure().setAND(and);
	}

	public static final void setChildren(Feature feature, Iterable<Feature> children) {
		feature.getStructure().setChildren(StreamSupport.stream(children.spliterator(), false).map(
			Feature::getStructure).collect(Collectors.toList()));
	}

	public static final void setConstraints(FeatureModel featureModel, final Iterable<Constraint> constraints) {

		featureModel.setConstraints(constraints);
	}

	public static final void setConstraintSelected(Feature feature, boolean selection) {

		feature.getProperty().selectConstraint(selection);
	}

	public static final void setContainedFeatures(Constraint constraint) {
	}

	public static final void setDescription(Feature feature, CharSequence description) {

		feature.getProperty().setDescription(description);
	}

	public static final void setFeatureOrderList(FeatureModel featureModel, final List<String> featureOrderList) {

		featureModel.setFeatureOrderList(featureOrderList);
	}

	public static final void setFeatureOrderUserDefined(FeatureModel featureModel, boolean featureOrderUserDefined) {

		featureModel.setFeatureOrderUserDefined(featureOrderUserDefined);
	}

	public static final void setFeatureTable(FeatureModel featureModel, final Hashtable<String, Feature> featureTable) {

		featureModel.setFeatureTable(featureTable);
	}

	public static void setHiddden(Feature feature, boolean hid) {

		feature.getStructure().setHidden(hid);
	}

	public static final void setHidden(Feature feature, boolean hid) {

		feature.getStructure().setHidden(hid);
	}

	public static final void setMandatory(Feature feature, boolean mandatory) {

		feature.getStructure().setMandatory(mandatory);
	}

	public static final void setMultiple(Feature feature, boolean multiple) {

		feature.getStructure().setMultiple(multiple);
	}

	public static final void setName(Feature feature, String name) {

		feature.setName(name);
	}

	public static final void setOr(Feature feature) {

		feature.getStructure().setOr();
	}

	public static final void setParent(Feature feature, Feature newParent) {

		feature.getStructure().setParent(newParent.getStructure());
	}

	public static void setRelevantConstraints(Feature bone) {

		final List<Constraint> constraintList = new LinkedList<>();
		for (final Constraint constraint : bone.getFeatureModel().getConstraints()) {
			for (final Feature f : constraint.getContainedFeatures()) {
				if (f.getName().equals(bone.getName())) {
					constraintList.add(constraint.clone(bone.getFeatureModel()));
					break;
				}
			}
		}
		bone.getStructure().setRelevantConstraints(constraintList);
	}

	public static final void setRoot(FeatureModel featureModel, Feature root) {

		featureModel.getStructure().setRoot(root.getStructure());
	}

	public static final String toString(Constraint constraint) {

		return constraint.toString();
	}

	public static final String toString(Feature feature) {

		return feature.toString();
	}

//	public static final String toString(Feature feature, boolean writeMarks) {
//
//		if (writeMarks) {
//			final String featureName = feature.getName();
//			if (featureName.contains(" ") || Operator.isOperatorName(featureName)) {
//				return "\"" + feature.getName() + "\"";
//			}
//			return feature.getName();
//		} else {
//			return feature.toString();
//		}
//	}

	public static final String toString(FeatureModel featureModel) {

		return featureModel.toString();
	}

	private static void tryRemoveConstraint(FeatureModel featureModel, List<Constraint> constraints, int index) {

		if ((index == -1) || (index >= constraints.size())) {
			throw new NoSuchElementException();
		} else {
			constraints.remove(index);
			featureModel.setConstraints(constraints);
		}
	}

	private FeatureModels() {
	}

	public CharSequence createValidJavaIdentifierFromString(CharSequence s) {

		final StringBuilder stringBuilder = new StringBuilder();
		int i = 0;
		for (; i < s.length(); i++) {
			if (Character.isJavaIdentifierStart(s.charAt(i))) {
				stringBuilder.append(s.charAt(i));
				i++;
				break;
			}
		}
		for (; i < s.length(); i++) {
			if (Character.isJavaIdentifierPart(s.charAt(i))) {
				stringBuilder.append(s.charAt(i));
			}
		}
		return stringBuilder.toString();
	}
}
