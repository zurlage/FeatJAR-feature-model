package org.spldev.featuremodel;

import org.spldev.featuremodel.mixins.CommonAttributesMixin;
import org.spldev.featuremodel.util.Mutable;

import java.util.Objects;

/**
 * A feature in a {@link FeatureModel} describes some functionality of a software system.
 * It is attached to some feature model and labels a {@link FeatureTree}.
 * For safe mutation, rely only on the methods of {@link Mutable}.
 *
 * @author Elias Kuiter
 */
public class Feature extends Element implements CommonAttributesMixin, Mutable<Feature, Feature.Mutator> {
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

	@Override
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}

	@Override
	public void invalidate() {
	}

	@Override
	public String toString() {
		return String.format("Feature{name=%s}", getName());
	}

	public class Mutator implements Mutable.Mutator<Feature>, CommonAttributesMixin.Mutator<Feature> {
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
