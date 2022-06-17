package org.spldev.featuremodel;

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
	protected final FeatureTree featureTree;

	public Feature(Identifier<?> identifier, PureFeatureModel featureModel) {
		super(identifier, featureModel);
		featureTree = new FeatureTree(this);
	}

	@Override
	public Set<Attribute<?>> getDefinableAttributes() {
		return featureModel.getDefinableFeatureAttributes();
	}

	public FeatureTree getFeatureTree() {
		return featureTree;
	}

	public String getName() {
		return getAttributeValue(Attribute.NAME).orElse("@" + getIdentifier());
	}

	public void setName(String name) {
		setAttributeValue(Attribute.NAME, name);
	}

	public Optional<String> getDescription() {
		return getAttributeValue(Attribute.DESCRIPTION);
	}

	public void setDescription(String description) {
		setAttributeValue(Attribute.DESCRIPTION, description);
	}

	public boolean isAbstract() {
		return getAttributeValue(Attribute.ABSTRACT).get();
	}

	public boolean isConcrete() {
		return !isAbstract();
	}

	public void setAbstract(boolean value) {
		setAttributeValue(Attribute.ABSTRACT, value);
	}

	public boolean isHidden() {
		return getAttributeValue(Attribute.HIDDEN).get();
	}

	public boolean isVisible() {
		return !isHidden();
	}

	public void setHidden(boolean value) {
		setAttributeValue(Attribute.HIDDEN, value);
	}
}
