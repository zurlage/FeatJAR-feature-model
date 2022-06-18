package org.spldev.featuremodel;

import org.spldev.featuremodel.mixin.*;

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
public class FeatureModel extends Element implements FeatureTreeMixin, ConstraintMixin, FeatureModelTreeMixin, FeatureOrderMixin, AttributeMixin, CacheMixin, Cloneable {
	protected final FeatureModelTree featureModelTree;
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();
	protected final Map<Identifier<?>, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
	protected final Set<FeatureModel> featureModelCache = Collections.synchronizedSet(new HashSet<>());

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
		return Collections.unmodifiableList(constraints);
	}

	@Override
	public FeatureOrder getFeatureOrder() {
		return featureOrder;
	}

	@Override
	public void setFeatureOrder(FeatureOrder featureOrder) {
		this.featureOrder = featureOrder;
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
}
