package org.spldev.featuremodel;

import org.spldev.featuremodel.mixins.*;

import java.util.*;

/**
 * Feature model
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class FeatureModel extends Element implements FeatureModelFeatureTreeMixin, FeatureModelConstraintMixin, FeatureModelFeatureOrderMixin, CommonAttributesMixin, MutableMixin<FeatureModel, FeatureModel.Mutator>, Cloneable { // CacheMixin
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();
	protected final Map<Identifier<?>, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
	protected final Set<FeatureModel> featureModelCache = Collections.synchronizedSet(new HashSet<>());
	protected Mutator mutator = null;

	public FeatureModel(Identifier<?> identifier) {
		super(identifier);
		final Feature root = new Feature(this);
		featureTree = root.getFeatureTree();
		//invalidateCaches();
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

//	@Override
//	public Map<Identifier<?>, Element> getElementCache() {
//		return elementCache;
//	}
//
//	@Override
//	public Set<Feature> getFeatureCache() {
//		return featureCache;
//	}
//
//	@Override
//	public Set<FeatureModel> getFeatureModelCache() {
//		return featureModelCache;
//	}

	@Override
	public Mutator getMutator() {
		return mutator == null ? (mutator = new Mutator()) : mutator;
	}

	// todo
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public FeatureModel clone() {
		throw new RuntimeException();
	}

	public class Mutator implements MutableMixin.Mutator<FeatureModel>, FeatureModelFeatureTreeMixin.Mutator, FeatureModelConstraintMixin.Mutator, FeatureModelFeatureOrderMixin.Mutator, CommonAttributesMixin.Mutator<FeatureModel> {
		@Override
		public FeatureModel getMutable() {
			return FeatureModel.this;
		}

		@Override
		public void setFeatureOrder(FeatureOrder featureOrder) {
			FeatureModel.this.featureOrder = featureOrder;
		}
	}
}
