package org.spldev.featuremodel;

import java.util.*;
import java.util.stream.Collectors;

import org.spldev.featuremodel.mixins.CommonAttributesMixin;
import org.spldev.featuremodel.util.Analyzable;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.featuremodel.util.Mutable;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.Formulas;

/**
 * A constraint describes some restriction of the valid {@link Configuration
 * configurations} represented by a {@link FeatureModel}. It is attached to some
 * feature model and represented as a {@link Formula} over {@link Feature
 * features}. For safe mutation, rely only on the methods of {@link Mutable}.
 *
 * @author Elias Kuiter
 */
public class Constraint extends Element implements Mutable<Constraint, Constraint.Mutator>,
	Analyzable<Constraint, Constraint.Analyzer> {
	protected final FeatureModel featureModel;
	protected Formula formula;
	protected Set<Feature> containedFeaturesCache = new HashSet<>();
	protected Mutator mutator;
	protected Analyzer analyzer;

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

	@Override
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}

	@Override
	public String toString() {
		return String.format("Constraint{formula=%s}", formula);
	}

	@Override
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	@Override
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public class Mutator implements org.spldev.featuremodel.util.Mutator<Constraint>,
		CommonAttributesMixin.Mutator<Constraint> {
		@Override
		public Constraint getMutable() {
			return Constraint.this;
		}

		public void setFormula(Formula formula) {
			Objects.requireNonNull(formula);
			Set<Identifier> identifiers = Formulas.getVariableNames(formula).stream()
				.map(getIdentifier().getFactory()::parse)
				.collect(Collectors.toSet());
			Optional<Identifier> unknownIdentifier = identifiers.stream().filter(identifier -> !featureModel.hasFeature(
				identifier)).findAny();
			if (unknownIdentifier.isPresent()) {
				throw new RuntimeException("encountered unknown identifier " + unknownIdentifier.get());
			}
			containedFeaturesCache = identifiers.stream()
				.map(featureModel::getFeature)
				.map(Optional::get)
				.collect(Collectors.toSet());
			Constraint.this.formula = formula;
		}

		public void remove() {
			getFeatureModel().mutate().removeConstraint(Constraint.this);
		}
	}

	public class Analyzer implements org.spldev.featuremodel.util.Analyzer<Constraint> {
		@Override
		public Constraint getAnalyzable() {
			return Constraint.this;
		}

		public boolean isRedundant() {
			return false;
		}

		// ...
	}
}
