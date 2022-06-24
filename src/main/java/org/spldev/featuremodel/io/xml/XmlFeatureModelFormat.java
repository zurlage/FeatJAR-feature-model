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
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.util.data.Problem;
import org.spldev.util.data.Problem.Severity;
import org.spldev.util.data.Result;
import org.spldev.util.io.format.Format;
import org.spldev.util.io.format.Input;
import org.spldev.util.io.format.ParseException;
import org.spldev.util.io.format.ParseProblem;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

import static org.spldev.formula.io.xml.XmlFeatureModelFormat.*;

/**
 * Parses and writes feature models from and to FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XmlFeatureModelFormat implements Format<FeatureModel> {
	protected final FeatureModel featureModel = new FeatureModel(Identifier.newCounter());
	protected final VariableMap variableMap = VariableMap.emptyMap();
	protected final Map<String, Identifier> nameToIdentifierMap = new HashMap<>();
	protected List<Problem> parseProblems = new ArrayList<>();

	@Override
	public String getFileExtension() {
		return "xml";
	}

	@Override
	public Result<FeatureModel> parse(Input source) {
		try {
			parseProblems.clear();
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(source.getReader()),
				new org.spldev.util.io.PositionalXMLHandler(doc));
			doc.getDocumentElement().normalize();
			return Result.of(readDocument(doc), parseProblems);
		} catch (final Exception e) {
			return Result.empty(new Problem(e));
		}
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	protected FeatureModel readDocument(Document doc) throws ParseException {
		final List<Element> elementList = getElement(doc, FEATURE_MODEL);
		if (elementList.size() == 1) {
			final Element e = elementList.get(0);
			parseStruct(getElement(e, STRUCT));
			new org.spldev.formula.io.xml.XmlFeatureModelFormat().parseConstraints(
				getElement(e, CONSTRAINTS),
				name -> variableMap.getVariable(nameToIdentifierMap.get(name).toString()),
				parseProblems,
				formula -> featureModel.mutate().createConstraint(formula));
		} else if (elementList.isEmpty()) {
			throw new ParseException("Not a feature model xml element!");
		} else {
			throw new ParseException("More than one feature model xml elements!");
		}
		return featureModel;
	}

	protected void parseFeatures(NodeList nodeList, Feature parent, boolean and) throws ParseException {
		final List<Element> elements = getElements(nodeList);
		if (parent == null) {
			if (elements.isEmpty()) {
				throw new ParseException("No root feature!");
			}
			if (elements.size() > 1) {
				throw new ParseException("Multiple root features!");
			}
		} else {
			if (elements.isEmpty()) {
				parseProblems.add(new ParseProblem("No feature in group!", 0, Severity.WARNING));
			}
		}
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case AND:
			case OR:
			case ALT:
			case FEATURE:
				parseFeature(parent, e, nodeName, and);
				break;
			default:
				break;
			}
		}
	}

	protected void parseFeature(Feature parent, final Element e, final String nodeName, boolean and)
		throws ParseException {
		boolean mandatory = false;
		String name = null;
		if (e.hasAttributes()) {
			final NamedNodeMap nodeMap = e.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				final Node node = nodeMap.item(i);
				final String attributeName = node.getNodeName();
				final String attributeValue = node.getNodeValue();
				if (attributeName.equals(MANDATORY)) {
					mandatory = attributeValue.equals(TRUE);
				} else if (attributeName.equals(NAME)) {
					name = attributeValue;
				}
			}
		}

		Feature feature;
		if (parent == null) {
			feature = featureModel.getRootFeature();
			featureModel.getRootFeature().mutate().setName(name);
			featureModel.getFeatureTree().mutate().setMandatory(and && mandatory);
		} else {
			feature = parent.mutate().createFeatureBelow();
			feature.mutate().setName(name);
			feature.getFeatureTree().mutate().setMandatory(and && mandatory);
		}
		nameToIdentifierMap.put(name, feature.getIdentifier());
		variableMap.addBooleanVariable(feature.getIdentifier().toString());

		if (e.hasChildNodes()) {
			switch (nodeName) {
			case AND:
				feature.getFeatureTree().mutate().setAnd();
				break;
			case OR:
				feature.getFeatureTree().mutate().setOr();
				break;
			case ALT:
				feature.getFeatureTree().mutate().setAlternative();
				break;
			}
			parseFeatures(e.getChildNodes(), feature, nodeName.equals(AND));
		} else if (!"feature".equals(nodeName)) {
			throw new ParseException("Empty group!");
		}
	}

	protected void parseStruct(List<Element> elements) throws ParseException {
		if (elements.isEmpty()) {
			throw new ParseException("No <struct> element!");
		}
		if (elements.size() > 1) {
			throw new ParseException("Multiple <struct> elements!");
		}
		parseFeatures(elements.get(0).getChildNodes(), null, false);
	}

	@Override
	public XmlFeatureModelFormat getInstance() {
		return new XmlFeatureModelFormat();
	}

	@Override
	public String getName() {
		return "FeatureIDE";
	}

}
