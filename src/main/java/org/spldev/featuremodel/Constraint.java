package org.spldev.featuremodel;

import java.util.*;
import java.util.stream.Collectors;

import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.Formulas;

/**
 * Constraint
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Marlen Bernier
 * @author Dawid Szczepanski
 * @author Elias Kuiter
 */
public class Constraint extends Element {
	protected Formula formula;
	protected Set<Feature> containedFeaturesCache = new HashSet<>();

	public Constraint(Identifier<?> identifier, PureFeatureModel featureModel, Formula formula) {
		super(identifier, featureModel);
		setFormula(formula);
	}

	@Override
	public Set<Attribute<?>> getDefinableAttributes() {
		return featureModel.getDefinableConstraintAttributes();
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		Objects.requireNonNull(this.formula);
		Set<Identifier<?>> identifiers = Formulas.getVariableNames(formula).stream()
				.map(featureModel.identifierFactory::fromString)
				.collect(Collectors.toSet());
		Optional<Identifier<?>> unknownIdentifier =
				identifiers.stream().filter(identifier -> !featureModel.hasFeature(identifier)).findAny();
		if (unknownIdentifier.isPresent()) {
			throw new RuntimeException("encountered unknown identifier " + unknownIdentifier.get());
		}
		containedFeaturesCache = identifiers.stream()
				.map(featureModel::getFeature)
				.map(Optional::get)
				.collect(Collectors.toSet());
		this.formula = formula;
	}

	public Set<Feature> getContainedFeatures() {
		return Collections.unmodifiableSet(containedFeaturesCache);
	}

	public Optional<String> getDescription() {
		return getAttributeValue(Attribute.DESCRIPTION);
	}

	public void setDescription(String description) {
		setAttributeValue(Attribute.DESCRIPTION, description);
	}
}
