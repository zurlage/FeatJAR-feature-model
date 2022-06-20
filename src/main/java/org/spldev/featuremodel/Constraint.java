package org.spldev.featuremodel;

import java.util.*;
import java.util.stream.Collectors;

import org.spldev.featuremodel.mixins.CommonAttributesMixin;
import org.spldev.featuremodel.mixins.MutableMixin;
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
public class Constraint extends Element implements MutableMixin<Constraint, Constraint.Mutator> {
	protected final FeatureModel featureModel;
	protected Formula formula;
	protected Set<Feature> containedFeaturesCache = new HashSet<>();
	protected Mutator mutator = null;

	public Constraint(FeatureModel featureModel, Formula formula) {
		super(featureModel.getNewIdentifier());
		Objects.requireNonNull(featureModel);
		this.featureModel = featureModel;
		getMutator().setFormula(formula); // todo efficient?
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public Formula getFormula() {
		return formula;
	}

	public Set<Feature> getContainedFeatures() {
		return containedFeaturesCache;
	}

	@Override
	public Mutator getMutator() {
		return mutator == null ? (mutator = new Mutator()) : mutator;
	}

	public class Mutator implements MutableMixin.Mutator<Constraint>, CommonAttributesMixin.Mutator<Constraint> {
		@Override
		public Constraint getMutable() {
			return Constraint.this;
		}

		public void setFormula(Formula formula) {
			Objects.requireNonNull(formula);
			Set<Identifier<?>> identifiers = Formulas.getVariableNames(formula).stream()
					.map(getIdentifier().getFactory()::fromString)
					.collect(Collectors.toSet());
			Optional<Identifier<?>> unknownIdentifier =
					identifiers.stream().filter(identifier -> !featureModel.hasFeature(identifier)).findAny();
			if (unknownIdentifier.isPresent()) {
				throw new RuntimeException("encountered unknown identifier " + unknownIdentifier.get()); // todo multimodel?
			}
			containedFeaturesCache = identifiers.stream()
					.map(featureModel::getFeature)
					.map(Optional::get)
					.collect(Collectors.toSet());
			Constraint.this.formula = formula;
		}
	}
}
