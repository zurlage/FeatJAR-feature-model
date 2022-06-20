package org.spldev.featuremodel;

import org.spldev.featuremodel.mixins.CommonAttributesMixin;
import org.spldev.featuremodel.mixins.MutableMixin;

import java.util.Objects;

/**
 * Feature
 *
 * @author Thomas Thuem
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 * @author Elias Kuiter
 */
public class Feature extends Element implements CommonAttributesMixin, MutableMixin<Feature, Feature.Mutator> {
	protected final FeatureModel featureModel;
	protected final FeatureTree featureTree;
	protected Mutator mutator = null;

	public Feature(FeatureModel featureModel) {
		super(featureModel.getNewIdentifier());
		Objects.requireNonNull(featureModel);
		this.featureModel = featureModel;
		featureTree = new FeatureTree(this);
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public FeatureTree getFeatureTree() {
		return featureTree;
	}

	public boolean isAbstract() {
		return getAttributeValue(Attributes.ABSTRACT);
	}

	public boolean isConcrete() {
		return !isAbstract();
	}

	public boolean isHidden() {
		return getAttributeValue(Attributes.HIDDEN);
	}

	public boolean isVisible() {
		return !isHidden();
	}

	@Override
	public Mutator getMutator() {
		return mutator == null ? (mutator = new Mutator()) : mutator;
	}

	public class Mutator implements MutableMixin.Mutator<Feature>, CommonAttributesMixin.Mutator<Feature> {
		@Override
		public Feature getMutable() {
			return Feature.this;
		}

		public void setAbstract(boolean value) {
			setAttributeValue(Attributes.ABSTRACT, value);
		}

		public void setHidden(boolean value) {
			setAttributeValue(Attributes.HIDDEN, value);
		}
	}
}
