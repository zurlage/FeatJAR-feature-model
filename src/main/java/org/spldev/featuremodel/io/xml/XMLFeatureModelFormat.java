/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.featuremodel.io.xml;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.formula.io.xml.AbstractXMLFeatureModelFormat;
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.util.io.format.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Parses and writes feature models from and to FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelFormat extends AbstractXMLFeatureModelFormat<FeatureModel, Feature> {
	protected final FeatureModel featureModel = new FeatureModel(Identifier.newCounter());
	protected final VariableMap variableMap = VariableMap.emptyMap();
	protected final Map<String, Identifier> nameToIdentifierMap = new HashMap<>();

	@Override
	public XMLFeatureModelFormat getInstance() {
		return new XMLFeatureModelFormat();
	}

	@Override
	public String getName() {
		return "FeatureIDE";
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return false;
	}

	@Override
	public FeatureModel parseDocument(Document document) throws ParseException {
		final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
		parseFeatures(getElement(featureModelElement, STRUCT));
		Optional<Element> constraintsElement = getOptionalElement(featureModelElement, CONSTRAINTS);
		if (constraintsElement.isPresent())
			parseConstraints(constraintsElement.get(),
					name -> variableMap.getVariable(
							Optional.ofNullable(nameToIdentifierMap.get(name)).map(Identifier::toString).orElse("")))
					.forEach(formula -> featureModel.mutate().createConstraint(formula));
		return featureModel;
	}

	@Override
	public void writeDocument(FeatureModel object, Document doc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Pattern getInputHeaderPattern() {
		return inputHeaderPattern;
	}

	@Override
	protected Feature createFeatureLabel(String name, Feature parentFeatureLabel, boolean mandatory) {
		Feature feature;
		if (parentFeatureLabel == null) {
			feature = featureModel.getRootFeature();
		} else {
			feature = parentFeatureLabel.mutate().createFeatureBelow();
		}
		feature.mutate().setName(name);
		feature.getFeatureTree().mutate().setMandatory(mandatory);
		nameToIdentifierMap.put(name, feature.getIdentifier());
		variableMap.addBooleanVariable(feature.getIdentifier().toString());
		return feature;
	}

	@Override
	protected void addAndGroup(Feature featureLabel, List<Feature> childFeatureLabels) {
		featureLabel.getFeatureTree().mutate().setAnd();
	}

	@Override
	protected void addOrGroup(Feature featureLabel, List<Feature> childFeatureLabels) {
		featureLabel.getFeatureTree().mutate().setOr();
	}

	@Override
	protected void addAlternativeGroup(Feature featureLabel, List<Feature> childFeatureLabels) {
		featureLabel.getFeatureTree().mutate().setAlternative();
	}
}
