package org.spldev.model;

import org.spldev.model.mixins.*;
import org.spldev.model.util.Analyzable;
import org.spldev.model.util.Identifier;
import org.spldev.model.util.Mutable;
import org.spldev.formula.structure.atomic.literal.VariableMap;

import java.util.*;

/**
 * A feature model is a representation of the space of valid
 * {@link Configuration configurations} for a software system. Here, we
 * implement feature diagrams (i.e., a {@link FeatureTree} labeled with features
 * and a list of {@link Constraint constraints}). For safe mutation, rely only
 * on the methods of {@link Mutable}.
 *
 * @author Elias Kuiter
 */
public class FeatureModel extends Element implements FeatureModelFeatureTreeMixin, FeatureModelConstraintMixin,
	FeatureModelFeatureOrderMixin, CommonAttributesMixin, FeatureModelCacheMixin,
	Mutable<FeatureModel, FeatureModel.Mutator>, Analyzable<FeatureModel, FeatureModel.Analyzer> {
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();

	protected final VariableMap variableMap = VariableMap.emptyMap(); // todo: get, set, mutate correctly (checks), pass map to createConstraint as Function<...>
	protected final Map<Identifier, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
	protected final Set<FeatureModel> featureModelCache = Collections.synchronizedSet(new HashSet<>()); // todo
	// calculate
																										// from tree
	protected Mutator mutator;
	protected Analyzer analyzer;
	// todo inv: this featuretree (w/o submodels) has one variablemap. variablemap
	// == features.

	public FeatureModel(Identifier identifier) {
		super(identifier);
		final Feature root = new Feature(this);
		featureTree = root.getFeatureTree();
		finishInternalMutation();
	}

	@Override
	public FeatureTree getFeatureTree() {
		return featureTree;
	}

	@Override
	public List<Constraint> getConstraints() {
		return constraints;
	}

	@Override
	public FeatureOrder getFeatureOrder() {
		return featureOrder;
	}

	//@Override //constraint mixin?
	public VariableMap getVariableMap() {
		return variableMap;
	}

	@Override
	public Map<Identifier, Element> getElementCache() {
		return elementCache;
	}

	@Override
	public Set<Feature> getFeatureCache() {
		return featureCache;
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
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	@Override
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	@Override
	public void finishInternalMutation() {
		FeatureModelCacheMixin.super.finishInternalMutation();
	}

	@Override
	public String toString() {
		return String.format("FeatureModel{features=%s, constraints=%s}", getFeatures(), constraints);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); // todo
	}

	public class Mutator implements org.spldev.model.util.Mutator<FeatureModel>,
		FeatureModelFeatureTreeMixin.Mutator, FeatureModelConstraintMixin.Mutator,
		FeatureModelFeatureOrderMixin.Mutator, CommonAttributesMixin.Mutator<FeatureModel>,
		FeatureModelCacheMixin.Mutator {
		@Override
		public FeatureModel getMutable() {
			return FeatureModel.this;
		}

		@Override
		public void setFeatureOrder(FeatureOrder featureOrder) {
			FeatureModel.this.featureOrder = featureOrder;
		}
	}

	public class Analyzer implements org.spldev.model.util.Analyzer<FeatureModel>,
		FeatureModelFeatureTreeMixin.Analyzer, FeatureModelConstraintMixin.Analyzer {
		@Override
		public FeatureModel getAnalyzable() {
			return FeatureModel.this;
		}
	}
}
