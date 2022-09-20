/*
 * Copyright (C) 2022 Elias Kuiter
 *
 * This file is part of model.
 *
 * model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import de.featjar.feature.model.mixins.CommonAttributesMixin;
import de.featjar.base.data.Analyzable;
import de.featjar.base.data.Mutable;

import java.util.Objects;

/**
 * A feature in a {@link FeatureModel} describes some functionality of a software system.
 * It is attached to a {@link FeatureModel} and labels a {@link FeatureTree}.
 * For safe mutation, rely only on the methods of {@link Mutable}.
 * A {@link Feature} is uniquely determined by its immutable {@link de.featjar.base.data.Identifier}
 * or name (obtained with {@link #getName()}).
 * In contrast to a feature's identifier, its name is mutable and should therefore be used sparsely
 * to avoid cache invalidation and renaming issues.
 *
 * @author Elias Kuiter
 */
public class Feature extends Element
        implements CommonAttributesMixin, Mutable<Feature, Feature.Mutator>, Analyzable<Feature, Feature.Analyzer> {
    protected final FeatureModel featureModel;
    protected final FeatureTree featureTree;
    protected Mutator mutator;
    protected Analyzer analyzer;

    public Feature(FeatureModel featureModel) {
        super(featureModel.getNewIdentifier());
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
        featureTree = new FeatureTree(this);
    }

    @Override
    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public FeatureTree getFeatureTree() {
        return featureTree;
    }

    public boolean isAbstract() {
        return (boolean) getAttributeValue(Attributes.ABSTRACT);
    }

    public boolean isConcrete() {
        return !isAbstract();
    }

    public boolean isHidden() {
        return (boolean) getAttributeValue(Attributes.HIDDEN);
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
    public Analyzer getAnalyzer() {
        return analyzer == null ? (analyzer = new Analyzer()) : analyzer;
    }

    @Override
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public String toString() {
        return String.format("Feature{name=%s}", getName());
    }

    public class Mutator implements de.featjar.base.data.Mutator<Feature>, CommonAttributesMixin.Mutator<Feature> {
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

        public boolean toggleAbstract() {
            return toggleAttributeValue(Attributes.ABSTRACT);
        }

        public boolean toggleHidden() {
            return toggleAttributeValue(Attributes.HIDDEN);
        }

        public void addFeatureBelow(Feature newFeature, int index) {
            getFeatureModel().mutate().addFeatureBelow(newFeature, Feature.this, index);
        }

        public void addFeatureBelow(Feature newFeature) {
            getFeatureModel().mutate().addFeatureBelow(newFeature, Feature.this);
        }

        public void addFeatureNextTo(Feature newFeature) {
            getFeatureModel().mutate().addFeatureNextTo(newFeature, Feature.this);
        }

        public Feature createFeatureBelow(int index) {
            return getFeatureModel().mutate().createFeatureBelow(Feature.this, index);
        }

        public Feature createFeatureBelow() {
            return getFeatureModel().mutate().createFeatureBelow(Feature.this);
        }

        public Feature createFeatureNextTo() {
            return getFeatureModel().mutate().createFeatureNextTo(Feature.this);
        }

        public void remove() {
            getFeatureModel().mutate().removeFeature(Feature.this);
        }
    }

    public class Analyzer implements de.featjar.base.data.Analyzer<Feature> {
        @Override
        public Feature getAnalyzable() {
            return Feature.this;
        }

        public boolean isDead() {
            return false;
        }

        // ...
    }
}
