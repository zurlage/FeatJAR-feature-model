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

import de.featjar.base.data.*;
import de.featjar.base.data.identifier.IIdentifier;
import de.featjar.feature.model.mixins.*;
import de.featjar.feature.model.order.IFeatureOrder;
import de.featjar.feature.model.order.PreOrderFeatureOrder;
import java.util.*;
import java.util.stream.Stream;

public class FeatureModel extends AFeatureModelElement implements IFeatureModel {
    protected final IFeatureModelTree featureModelTree;
    protected final IFeatureTree featureTree;
    protected final List<IConstraint> constraints = Collections.synchronizedList(new ArrayList<>());
    protected IFeatureOrder featureOrder = new PreOrderFeatureOrder();
    protected final LinkedHashMap<IIdentifier, IFeatureModelElement> elementCache = Maps.empty();
    protected final LinkedHashSet<IFeature> featureCache = Sets.empty();
    protected IFeatureModel.Mutator mutator;

    public FeatureModel(IIdentifier identifier) {
        super(identifier);
        featureModelTree = new FeatureModelTree(this);
        final Feature root = new Feature(this);
        featureTree = root.getFeatureTree();
        finishInternalMutation();
    }

    public FeatureModelTree getFeatureModelTree() {
        return null;
    }

    @Override
    public FeatureModel getFeatureModel() {
        return this;
    }

    @Override
    public IFeatureTree getFeatureTree() {
        return featureTree;
    }

    @Override
    public IFeatureOrder getFeatureOrder() {
        return featureOrder;
    }

    @Override
    public void finishInternalMutation() {
        LinkedHashSet<IFeature> features = IFeatureModel.super.getFeatures();

        elementCache.clear();
        Stream.concat(features.stream(), getConstraints().stream()).forEach(element -> {
            if (elementCache.get(element.getIdentifier()) != null)
                throw new RuntimeException("duplicate identifier " + element.getIdentifier());
            elementCache.put(element.getIdentifier(), element);
        });

        featureCache.clear();
        featureCache.addAll(features);
    }

    @Override
    public LinkedHashSet<IFeature> getFeatures() {
        return featureCache;
    }

    @Override
    public Result<IFeature> getFeature(IIdentifier identifier) {
        Objects.requireNonNull(identifier);
        IFeatureModelElement featureModelElement = elementCache.get(identifier);
        if (!(featureModelElement instanceof Feature)) return Result.empty();
        return Result.of((IFeature) featureModelElement);
    }

    @Override
    public List<IConstraint> getConstraints() {
        return constraints;
    }

    @Override
    public Result<IConstraint> getConstraint(IIdentifier identifier) {
        Objects.requireNonNull(identifier);
        IFeatureModelElement featureModelElement = elementCache.get(identifier);
        if (!(featureModelElement instanceof Constraint)) return Result.empty();
        return Result.of((IConstraint) featureModelElement);
    }

    @Override
    public IFeatureModel.Mutator getMutator() {
        return mutator == null ? (mutator = new Mutator()) : mutator;
    }

    @Override
    public void setMutator(IFeatureModel.Mutator mutator) {
        this.mutator = mutator;
    }

    @Override
    public String toString() {
        return String.format("FeatureModel{features=%s, constraints=%s}", getFeatures(), constraints);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(); // TODO
    }

    public class Mutator implements IFeatureModel.Mutator {
        @Override
        public FeatureModel getMutable() {
            return FeatureModel.this;
        }

        @Override
        public void setFeatureOrder(IFeatureOrder featureOrder) {
            FeatureModel.this.featureOrder = featureOrder;
        }

        @Override
        public IFeature newFeature() {
            return new Feature(getMutable());
        }

        @Override
        public IConstraint newConstraint() {
            return new Constraint(getMutable());
        }

        @Override
        public void addFeatureBelow(IFeature newFeature, IFeature parentFeature, int index) {
            IFeatureModel.Mutator.super.addFeatureBelow(newFeature, parentFeature, index);
            getMutable().featureCache.add(newFeature);
            getMutable().elementCache.put(newFeature.getIdentifier(), newFeature);
        }

        @Override
        public void removeFeature(IFeature feature) {
            IFeatureModel.Mutator.super.removeFeature(feature);
            getMutable().featureCache.remove(feature);
            getMutable().elementCache.remove(feature.getIdentifier());
        }

        @Override
        public void setConstraint(int index, IConstraint constraint) {
            IConstraint oldConstraint = getMutable().getConstraints().get(index);
            IFeatureModel.Mutator.super.setConstraint(index, constraint);
            getMutable().elementCache.remove(oldConstraint.getIdentifier());
            getMutable().elementCache.put(constraint.getIdentifier(), constraint);
        }

        @Override
        public void addConstraint(IConstraint newConstraint, int index) {
            IFeatureModel.Mutator.super.addConstraint(newConstraint, index);
            getMutable().elementCache.put(newConstraint.getIdentifier(), newConstraint);
        }

        @Override
        public void removeConstraint(IConstraint constraint) {
            IFeatureModel.Mutator.super.removeConstraint(constraint);
            getMutable().elementCache.remove(constraint.getIdentifier());
        }

        @Override
        public IConstraint removeConstraint(int index) {
            IConstraint constraint = IFeatureModel.Mutator.super.removeConstraint(index);
            getMutable().elementCache.remove(constraint.getIdentifier());
            return constraint;
        }
    }
}
