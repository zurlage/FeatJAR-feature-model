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
public class FeatureModel extends Element implements FeatureTreeMixin, ConstraintMixin, FeatureModelTreeMixin, FeatureOrderMixin, CacheMixin, Cloneable {
	protected final FeatureModelTree featureModelTree;
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();
	protected final Map<Identifier<?>, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
	protected final Set<FeatureModel> featureModelCache = Collections.synchronizedSet(new HashSet<>());

	protected Set<Attribute<?>> definableAttributes = new HashSet<>();
	protected Set<Attribute<?>> definableFeatureAttributes = new HashSet<>();
	protected Set<Attribute<?>> definableConstraintAttributes = new HashSet<>();

	{
		definableFeatureAttributes.add(Attributes.NAME);
		definableFeatureAttributes.add(Attributes.DESCRIPTION);
		definableFeatureAttributes.add(Attributes.HIDDEN);
		definableFeatureAttributes.add(Attributes.ABSTRACT);
		definableConstraintAttributes.add(Attributes.DESCRIPTION);
		definableAttributes.add(Attributes.NAME);
		definableAttributes.add(Attributes.DESCRIPTION);
	}

	public FeatureModel(Identifier<?> identifier) {
		super(identifier);
		final Feature root = new Feature(this);
		featureModelTree = new FeatureModelTree(this);
		featureTree = root.getFeatureTree();
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

	public Set<Attribute<?>> getDefinableFeatureAttributes() {
		return definableFeatureAttributes;
	}

	public void setDefinableFeatureAttributes(Set<Attribute<?>> definableFeatureAttributes) {
		this.definableFeatureAttributes = definableFeatureAttributes;
	}

	public Set<Attribute<?>> getDefinableConstraintAttributes() {
		return definableConstraintAttributes;
	}

	public void setDefinableConstraintAttributes(Set<Attribute<?>> definableConstraintAttributes) {
		this.definableConstraintAttributes = definableConstraintAttributes;
	}

	@Override
	public Set<Attribute<?>> getDefinableAttributes() {
		return definableAttributes;
	}

	public void setDefinableAttributes(Set<Attribute<?>> definableAttributes) {
		this.definableAttributes = definableAttributes;
	}

	public String getName() {
		return getAttributeValue(Attributes.NAME);
	}

	public void setName(String name) {
		setAttributeValue(Attributes.NAME, name);
	}

	public Optional<String> getDescription() {
		return getAttributeValue(Attributes.DESCRIPTION);
	}

	public void setDescription(String description) {
		setAttributeValue(Attributes.DESCRIPTION, description);
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
