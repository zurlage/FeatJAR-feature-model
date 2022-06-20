package org.spldev.featuremodel;

import org.spldev.featuremodel.mixin.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Feature model
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class FeatureModel extends Element implements FeatureTreeMixin, ConstraintMixin, FeatureModelTreeMixin, FeatureOrderMixin, AttributeMixin, CacheMixin, Cloneable {
	protected final FeatureModelTree featureModelTree;
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();
	protected final Map<Identifier<?>, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
	protected final Set<FeatureModel> featureModelCache = Collections.synchronizedSet(new HashSet<>());
	protected final Mutator mutator = new Mutator(this);

	public FeatureModel(Identifier<?> identifier) {
		super(identifier);
		final Feature root = new Feature(this);
		featureModelTree = new FeatureModelTree(this);
		featureTree = root.getFeatureTree();
		invalidateCaches();
	}

	@Override
	public FeatureModelTree getFeatureModelTree() {
		return featureModelTree;
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

	@Override
	public Map<Identifier<?>, Element> getElementCache() {
		return elementCache;
	}

	@Override
	public Set<Feature> getFeatureCache() {
		return featureCache;
	}

	@Override
	public Set<FeatureModel> getFeatureModelCache() {
		return featureModelCache;
	}

	public FeatureModel mutate(Consumer<Mutator> mutatorConsumer) {
		mutatorConsumer.accept(mutator);
		return this;
	}

	public <T> T mutateReturn(Function<Mutator, T> mutatorFunction) {
		return mutatorFunction.apply(mutator);
	}

	public void unsafeMutate(Runnable r) {
		try {
			r.run();
		} finally {
			invalidateCaches();
		}
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

	public static class Mutator implements FeatureTreeMixin.Mutator, ConstraintMixin.Mutator, FeatureOrderMixin.Mutator, AttributeMixin.Mutator {
		protected final FeatureModel featureModel;

		public Mutator(FeatureModel featureModel) {
			this.featureModel = featureModel;
		}

		@Override
		public FeatureModel getFeatureModel() {
			return featureModel;
		}

		@Override
		public void setFeatureOrder(FeatureOrder featureOrder) {
			featureModel.featureOrder = featureOrder;
		}
	}

}
