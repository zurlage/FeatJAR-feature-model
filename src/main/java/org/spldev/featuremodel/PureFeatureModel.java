package org.spldev.featuremodel;

import org.spldev.formula.structure.Formula;
import org.spldev.util.data.Result;
import org.spldev.util.tree.Trees;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Feature model
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class PureFeatureModel extends Element implements Cloneable {
	protected static final Supplier<Identifier.Factory<UUID>> DEFAULT_IDENTIFIER_FACTORY_SUPPLIER = Identifier.Factory.UUID::new;

	protected final Identifier.Factory<?> identifierFactory;
	protected final FeatureModelTree featureModelTree;
	protected final FeatureTree featureTree;
	protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
	protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();

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

	public PureFeatureModel(Identifier.Factory<?> identifierFactory) {
		super(identifierFactory.get());
		Objects.requireNonNull(identifierFactory);
		this.identifierFactory = identifierFactory;
		final Feature root = new Feature(identifierFactory.get(), this);
		featureModelTree = new FeatureModelTree(this);
		featureTree = root.getFeatureTree();
	}

	public PureFeatureModel() {
		this(DEFAULT_IDENTIFIER_FACTORY_SUPPLIER.get());
	}

	public Identifier.Factory<?> getIdentifierFactory() {
		return identifierFactory;
	}

	public FeatureModelTree getFeatureModelTree() {
		return featureModelTree;
	}

	public Set<PureFeatureModel> getFeatureModels() {
		return Trees.parallelStream(featureModelTree).map(FeatureModelTree::getFeatureModel).collect(Collectors.toSet());
	}

	public Optional<PureFeatureModel> getFeatureModel(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		return getFeatureModels().stream().filter(featureModel -> featureModel.getIdentifier().equals(identifier)).findFirst();
	}

	public FeatureTree getFeatureTree() {
		return featureTree;
	}

	public Set<Feature> getFeatures() {
		return Trees.parallelStream(featureTree).map(FeatureTree::getFeature).collect(Collectors.toSet());
	}

	public List<Feature> getOrderedFeatures() {
		return featureOrder.apply(this);
	}

	public int getNumberOfFeatures() {
		return getFeatures().size();
	}

	public Feature getRootFeature() {
		return featureTree.getFeature();
	}

	public Optional<Feature> getFeature(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		return getFeatures().stream().filter(feature -> feature.getIdentifier().equals(identifier)).findFirst();
	}

	public boolean hasFeature(Identifier<?> identifier) {
		return getFeature(identifier).isPresent();
	}

	public boolean hasFeature(Feature feature) {
		return hasFeature(feature.getIdentifier());
	}

	public void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
		Objects.requireNonNull(newFeature);
		Objects.requireNonNull(parentFeature);
		if (hasFeature(newFeature) || !hasFeature(parentFeature)) {
			throw new IllegalArgumentException();
		}
		parentFeature.getFeatureTree().addChild(index, newFeature.getFeatureTree());
	}

	public void addFeatureBelow(Feature newFeature, Feature parentFeature) {
		Objects.requireNonNull(newFeature);
		Objects.requireNonNull(parentFeature);
		addFeatureBelow(newFeature, parentFeature, parentFeature.getFeatureTree().getNumberOfChildren());
	}

	public void addFeatureNextTo(Feature newFeature, Feature siblingFeature) {
		Objects.requireNonNull(newFeature);
		Objects.requireNonNull(siblingFeature);
		if (siblingFeature.getFeatureTree().isRoot() || !hasFeature(siblingFeature)) {
			throw new IllegalArgumentException();
		}
		addFeatureBelow(newFeature,
				siblingFeature.getFeatureTree().getParent().get().getFeature(),
				siblingFeature.getFeatureTree().getIndex().get() + 1);
	}

	public Feature createFeatureBelow(Feature parentFeature, int index) {
		Feature newFeature = new Feature(identifierFactory.get(), this);
		addFeatureBelow(newFeature, parentFeature, index);
		return newFeature;
	}

	public Feature createFeatureBelow(Feature parentFeature) {
		Feature newFeature = new Feature(identifierFactory.get(), this);
		addFeatureBelow(newFeature, parentFeature);
		return newFeature;
	}

	public Feature createFeatureNextTo(Feature siblingFeature) {
		Feature newFeature = new Feature(identifierFactory.get(), this);
		addFeatureNextTo(newFeature, siblingFeature);
		return newFeature;
	}

	public void removeFeature(Feature feature) {
		Objects.requireNonNull(feature);
		if (feature.equals(getRootFeature()) || !hasFeature(feature)) {
			throw new IllegalArgumentException();
		}

		final FeatureTree parentFeatureTree = feature.getFeatureTree().getParent().get();

		if (parentFeatureTree.getNumberOfChildren() == 1) {
			if (feature.getFeatureTree().isAnd()) {
				parentFeatureTree.setAnd();
			} else if (feature.getFeatureTree().isAlternative()) {
				parentFeatureTree.setAlternative();
			} else {
				parentFeatureTree.setOr();
			}
		}

		final int index = feature.getFeatureTree().getIndex().get();
		while (feature.getFeatureTree().hasChildren()) {
			parentFeatureTree.addChild(
					index,
					feature.getFeatureTree().removeChild(feature.getFeatureTree().getNumberOfChildren() - 1));
		}

		parentFeatureTree.removeChild(feature.getFeatureTree());
	}

	public List<Constraint> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	public Optional<Constraint> getConstraint(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		return constraints.stream().filter(constraint -> constraint.getIdentifier().equals(identifier)).findFirst();
	}

	public boolean hasConstraint(Identifier<?> identifier) {
		return getConstraint(identifier).isPresent();
	}

	public boolean hasConstraint(Constraint constraint) {
		return hasConstraint(constraint.getIdentifier());
	}

	public Optional<Integer> getConstraintIndex(Constraint constraint) {
		Objects.requireNonNull(constraint);
		return Result.indexToOptional(constraints.indexOf(constraint));
	}

	public int getNumberOfConstraints() {
		return constraints.size();
	}

	public void setConstraint(int index, Constraint constraint) {
		Objects.requireNonNull(constraint);
		if (hasConstraint(constraint)) {
			throw new IllegalArgumentException();
		}
		constraints.set(index, constraint);
	}

	public void setConstraints(Iterable<Constraint> constraints) {
		Objects.requireNonNull(constraints);
		this.constraints.clear();
		constraints.forEach(this::addConstraint);
	}

	public void addConstraint(Constraint newConstraint, int index) {
		Objects.requireNonNull(newConstraint);
		if (hasConstraint(newConstraint)) {
			throw new IllegalArgumentException();
		}
		constraints.add(index, newConstraint);
	}

	public void addConstraint(Constraint newConstraint) {
		addConstraint(newConstraint, constraints.size());
	}

	public Constraint createConstraint(Formula formula) {
		Constraint newConstraint = new Constraint(identifierFactory.get(), this, formula);
		addConstraint(newConstraint);
		return newConstraint;
	}

	public Constraint createConstraint(Formula formula, int index) {
		Constraint newConstraint = new Constraint(identifierFactory.get(), this, formula);
		addConstraint(newConstraint, index);
		return newConstraint;
	}

	public void removeConstraint(Constraint constraint) {
		Objects.requireNonNull(constraint);
		if (!hasConstraint(constraint)) {
			throw new IllegalArgumentException();
		}
		constraints.remove(constraint);
	}

	public Constraint removeConstraint(int index) {
		return constraints.remove(index);
	}

	public FeatureOrder getFeatureOrder() {
		return featureOrder;
	}

	public void setFeatureOrder(FeatureOrder featureOrder) {
		this.featureOrder = featureOrder;
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
	public PureFeatureModel clone() {
		throw new RuntimeException();
	}
}
