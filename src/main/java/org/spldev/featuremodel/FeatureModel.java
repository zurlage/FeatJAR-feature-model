package org.spldev.featuremodel;

import java.util.*;
import java.util.stream.Stream;

/**
 * Feature model
 * assumes that features/constraints are only added/deleted through the feature model class, not manually
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class FeatureModel extends PureFeatureModel {
	protected final Map<Identifier<?>, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());

	public FeatureModel(Identifier.Factory<?> identifierFactory) {
		super(identifierFactory);
		refreshElementCache();
		refreshFeatureCache();
	}

	public FeatureModel() {
		this(DEFAULT_IDENTIFIER_FACTORY_SUPPLIER.get());
	}

	public void refreshElementCache() {
		elementCache.clear();
		Stream.concat(super.getFeatures().stream(), constraints.stream())
				.forEach(element -> elementCache.put(element.getIdentifier(), element));
	}

	public void refreshFeatureCache() {
		featureCache.clear();
		featureCache.addAll(super.getFeatures());
	}

	@Override
	public Optional<Feature> getFeature(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		Element element = elementCache.get(identifier);
		if (!(element instanceof Feature))
			return Optional.empty();
		return Optional.of((Feature) element);
	}

	@Override
	public void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
		super.addFeatureBelow(newFeature, parentFeature, index);
		featureCache.add(newFeature);
		elementCache.put(newFeature.getIdentifier(), newFeature);
	}

	@Override
	public void removeFeature(Feature feature) {
		super.removeFeature(feature);
		featureCache.remove(feature);
		elementCache.remove(feature.getIdentifier());
	}

	@Override
	public Optional<Constraint> getConstraint(Identifier<?> identifier) {
		Objects.requireNonNull(identifier);
		Element element = elementCache.get(identifier);
		if (!(element instanceof Constraint))
			return Optional.empty();
		return Optional.of((Constraint) element);
	}

	@Override
	public void setConstraint(int index, Constraint constraint) {
		elementCache.remove(constraints.get(index).getIdentifier());
		super.setConstraint(index, constraint);
		elementCache.put(constraint.getIdentifier(), constraint);
	}

	@Override
	public void addConstraint(Constraint newConstraint, int index) {
		super.addConstraint(newConstraint, index);
		elementCache.put(newConstraint.getIdentifier(), newConstraint);
	}

	@Override
	public void removeConstraint(Constraint constraint) {
		super.removeConstraint(constraint);
		elementCache.remove(constraint.getIdentifier());
	}

	@Override
	public Constraint removeConstraint(int index) {
		Constraint constraint = super.removeConstraint(index);
		elementCache.remove(constraint.getIdentifier());
		return constraint;
	}
}
