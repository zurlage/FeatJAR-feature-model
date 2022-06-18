package org.spldev.featuremodel;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Feature
 *
 * @author Thomas Thuem
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class Feature extends Element {
	protected final PureFeatureModel featureModel;

	protected final FeatureTree featureTree;
	public Feature(Identifier<?> identifier, PureFeatureModel featureModel) {
		super(identifier);
		Objects.requireNonNull(featureModel);
		this.featureModel = featureModel;
		featureTree = new FeatureTree(this);
	}

	@Override
	public Set<Attribute<?>> getDefinableAttributes() {
		return featureModel.getDefinableFeatureAttributes();
	}

	public PureFeatureModel getFeatureModel() {
		return featureModel;
	}

	public FeatureTree getFeatureTree() {
		return featureTree;
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

	public boolean isAbstract() {
		return getAttributeValue(Attributes.ABSTRACT);
	}

	public boolean isConcrete() {
		return !isAbstract();
	}

	public void setAbstract(boolean value) {
		setAttributeValue(Attributes.ABSTRACT, value);
	}

	public boolean isHidden() {
		return getAttributeValue(Attributes.HIDDEN);
	}

	public boolean isVisible() {
		return !isHidden();
	}

	public void setHidden(boolean value) {
		setAttributeValue(Attributes.HIDDEN, value);
	}
}
