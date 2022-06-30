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

import org.spldev.featuremodel.*;
import org.spldev.featuremodel.io.AttributeIO;
import org.spldev.featuremodel.util.Attributable;
import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.formula.io.xml.AbstractXMLFeatureModelFormat;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.formula.structure.term.Variable;
import org.spldev.util.data.Problem;
import org.spldev.util.io.format.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Parses and writes feature models from and to FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelFormat extends AbstractXMLFeatureModelFormat<FeatureModel, Feature, Constraint> {
	public static final String NAMESPACE = XMLFeatureModelFormat.class.getCanonicalName();
	public static final String GRAPHICS_NAMESPACE = "<graphics>"; // todo
	public static final String CALCULATIONS_NAMESPACE = "<calculations>"; // todo
	protected static final String COMMENTS = "comments";
	protected static final String C = "c";
	protected static final String FEATURE_ORDER = "featureOrder";
	protected static final String USER_DEFINED = "userDefined";
	protected static final String KEY = "key";
	protected static final String VALUE = "value";
	protected static final String DATA_TYPE = "data-type";

	protected static final String PROPERTIES = "properties";
	protected static final String CALCULATIONS = "calculations";
	protected static final String CALCULATE_FEATURES = "Features";
	protected static final String CALCULATE_REDUNDANT = "Redundant";
	protected static final String CALCULATE_TAUTOLOGY = "Tautology";
	protected static final String CALCULATE_CONSTRAINTS = "Constraints";
	protected static final String CALCULATE_AUTO = "Auto";

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
		parseFeatureTree(getElement(featureModelElement, STRUCT));
		Optional<Element> element = getOptionalElement(featureModelElement, CONSTRAINTS);
		if (element.isPresent())
			parseConstraints(element.get(), this::getVariable);
		element = getOptionalElement(featureModelElement, COMMENTS);
		if (element.isPresent())
			parseComments(element.get());
		element = getOptionalElement(featureModelElement, FEATURE_ORDER);
		if (element.isPresent())
			parseFeatureOrder(List.of(element.get()));
		element = getOptionalElement(featureModelElement, PROPERTIES);
		if (element.isPresent())
			parseFeatureModelProperties(element.get());
		element = getOptionalElement(featureModelElement, CALCULATIONS);
		element.ifPresent(this::parseCalculations);
		return featureModel;
	}

	protected Optional<Feature> getFeature(String name) {
		return Optional.ofNullable(nameToIdentifierMap.get(name)).flatMap(featureModel::getFeature);
	}

	protected Optional<Variable<?>> getVariable(String name) {
		return variableMap.getVariable(
				Optional.ofNullable(nameToIdentifierMap.get(name)).map(Identifier::toString).orElse(""));
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
	protected Feature createFeatureLabel(String name, Feature parentFeatureLabel, boolean mandatory, boolean _abstract, boolean hidden) throws ParseException {
		Feature feature;
		if (parentFeatureLabel == null) {
			feature = featureModel.getRootFeature();
		} else {
			feature = parentFeatureLabel.mutate().createFeatureBelow();
		}
		feature.mutate(mutator -> {
			mutator.setName(name);
			mutator.setAbstract(_abstract);
			mutator.setHidden(hidden);
		});
		feature.getFeatureTree().mutate().setMandatory(mandatory);
		if (nameToIdentifierMap.get(name) != null) {
			throw new ParseException("Duplicate feature name!");
		}
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

	@Override
	protected void addFeatureMetadata(Feature featureLabel, Element e) throws ParseException {
		switch (e.getNodeName()) {
			case DESCRIPTION:
				featureLabel.mutate().setDescription(getDescription(e));
				break;
			case GRAPHICS:
				parseProperty(featureLabel, e, GRAPHICS_NAMESPACE);
				break;
			case PROPERTY:
				parseProperty(featureLabel, e, NAMESPACE);
				break;
		}
	}

	@Override
	protected Constraint createConstraintLabel() {
		return featureModel.mutate().createConstraint();
	}

	@Override
	protected void addConstraint(Constraint constraintLabel, Formula formula) {
		constraintLabel.mutate().setFormula(formula);
	}

	@Override
	protected void addConstraintMetadata(Constraint constraintLabel, Element e) throws ParseException {
		switch (e.getNodeName()) {
			case DESCRIPTION:
				constraintLabel.mutate().setDescription(getDescription(e));
				break;
			case GRAPHICS:
				parseProperty(constraintLabel, e, GRAPHICS_NAMESPACE);
				break;
			case PROPERTY:
				parseProperty(constraintLabel, e, NAMESPACE);
				break;
			case TAGS:
				constraintLabel.mutate().setTags(getTags(e));
		}
	}

	protected void parseComments(Element element) throws ParseException {
		for (final Element e1 : getElements(element.getChildNodes())) {
			if (e1.getNodeName().equals(C)) {
				featureModel.mutate().setDescription(featureModel.getDescription().orElse("") + "\n" + e1.getTextContent());
			} else {
				addParseProblem("Unknown comment attribute: " + e1.getNodeName(), e1, Problem.Severity.WARNING);
			}
		}
	}

	protected void parseFeatureOrder(List<Element> elements) throws ParseException {
		final ArrayList<String> order = new ArrayList<>(featureModel.getNumberOfFeatures());
		boolean userDefined = false;
		for (final Element e : elements) {
			if (e.hasAttributes()) {
				final NamedNodeMap nodeMap = e.getAttributes();
				for (int i = 0; i < nodeMap.getLength(); i++) {
					final Node node = nodeMap.item(i);
					final String attributeName = node.getNodeName();
					final String attributeValue = node.getNodeValue();
					if (attributeName.equals(USER_DEFINED)) {
						userDefined = attributeValue.equals(TRUE);
					} else if (attributeName.equals(NAME)) {
						if (getFeature(attributeValue).isPresent()) {
							order.add(attributeValue);
						} else {
							addParseProblem("Feature \"" + attributeValue + "\" does not exists", e, Problem.Severity.ERROR);
						}
					} else {
						addParseProblem("Unknown feature order attribute: " + attributeName, e, Problem.Severity.ERROR);
					}

				}
			}
			if (e.hasChildNodes()) {
				parseFeatureOrder(getElements(e.getChildNodes()));
			}
		}
		if (!order.isEmpty()) {
			List<Feature> featureList = order.stream()
					.map(nameToIdentifierMap::get)
					.map(featureModel::getFeature)
					.map(Optional::orElseThrow)
					.collect(Collectors.toList());
			featureModel.mutate().setFeatureOrder(FeatureOrder.ofList(featureList));
		}
		featureModel.getFeatureOrder().mutate().setUserDefined(userDefined);
	}

	protected String getDescription(Node e) {
		String description = e.getTextContent();
		// NOTE: THe following code is used for backwards compatibility. It replaces spaces and tabs that were added to the XML for indentation, but don't
		// belong to the actual description.
		if (description != null) {
			description = description.replaceAll("(\r\n|\r|\n)\\s*", "\n").replaceAll("\\A\n|\n\\Z", "");
		}
		return description;
	}

	protected Set<String> getTags(final Node e) {
		final String[] tagArray = e.getTextContent().split(",");
		return new HashSet<>(Arrays.asList(tagArray));
	}

	protected void parseProperty(org.spldev.featuremodel.Element element, Element e, String namespace) throws ParseException {
		if (!e.hasAttribute(KEY) || !e.hasAttribute(VALUE)) {
			addParseProblem("Missing one of the required attributes: " + KEY + " or " + VALUE, e, Problem.Severity.WARNING);
		} else {
			String typeString = e.hasAttribute(DATA_TYPE) ? e.getAttribute(DATA_TYPE) : "string";
			final String name = e.getAttribute(KEY);
			final String valueString = e.getAttribute(VALUE);
			parseProblems.addAll(AttributeIO.parseAndSetAttributeValue(element, namespace, name, typeString, valueString));
		}
	}

	protected void parseFeatureModelProperties(Element e) throws ParseException {
		for (final Element propertyElement : getElements(e.getChildNodes())) {
			final String nodeName = propertyElement.getNodeName();
			switch (nodeName) {
				case GRAPHICS:
					parseProperty(featureModel, propertyElement, GRAPHICS_NAMESPACE);
					break;
				case CALCULATIONS:
					parseProperty(featureModel, propertyElement, CALCULATIONS_NAMESPACE);
					break;
				case PROPERTY:
					parseProperty(featureModel, propertyElement, NAMESPACE);
					break;
			}
		}
	}

	protected void parseCalculations(Element e) {
		parseAttribute(e, CALCULATE_AUTO);
		parseAttribute(e, CALCULATE_CONSTRAINTS);
		parseAttribute(e, CALCULATE_FEATURES);
		parseAttribute(e, CALCULATE_REDUNDANT);
		parseAttribute(e, CALCULATE_TAUTOLOGY);
	}

	private void parseAttribute(final Element e, final String key) {
		if (e.hasAttribute(key)) {
			parseProblems.addAll(AttributeIO.parseAndSetAttributeValue(featureModel, CALCULATIONS_NAMESPACE, key, "bool", e.getAttribute(key)));
		}
	}

}
