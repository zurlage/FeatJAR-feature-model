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
package de.featjar.feature.model.mixins;

import de.featjar.feature.model.Constraint;
import de.featjar.feature.model.Element;
import de.featjar.feature.model.Feature;
import de.featjar.feature.model.FeatureModel;
import de.featjar.base.data.Identifier;

import java.util.*;
import java.util.stream.Stream;

/**
 * Cache. assumes that features/constraints are only added/deleted through the
 * feature model class, not manually
 *
 * @author Elias Kuiter
 */
public interface FeatureModelCacheMixin extends FeatureModelFeatureTreeMixin, FeatureModelConstraintMixin {
    Map<Identifier, Element> getElementCache();

    LinkedHashSet<Feature> getFeatureCache();

    default void finishInternalMutation() {
        LinkedHashSet<Feature> features = FeatureModelFeatureTreeMixin.super.getFeatures();

        getElementCache().clear();
        Stream.concat(features.stream(), getConstraints().stream()).forEach(element -> {
            if (getElementCache().get(element.getIdentifier()) != null)
                throw new RuntimeException("duplicate identifier " + element.getIdentifier());
            getElementCache().put(element.getIdentifier(), element);
        });

        getFeatureCache().clear();
        getFeatureCache().addAll(features);
    }

    @Override
    default LinkedHashSet<Feature> getFeatures() {
        return getFeatureCache();
    }

    @Override
    default Optional<Feature> getFeature(Identifier identifier) {
        Objects.requireNonNull(identifier);
        Element element = getElementCache().get(identifier);
        if (!(element instanceof Feature)) return Optional.empty();
        return Optional.of((Feature) element);
    }

    @Override
    default Optional<Constraint> getConstraint(Identifier identifier) {
        Objects.requireNonNull(identifier);
        Element element = getElementCache().get(identifier);
        if (!(element instanceof Constraint)) return Optional.empty();
        return Optional.of((Constraint) element);
    }

    interface Mutator
            extends de.featjar.base.data.Mutator<FeatureModel>,
                    FeatureModelFeatureTreeMixin.Mutator,
                    FeatureModelConstraintMixin.Mutator {
        @Override
        default void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
            FeatureModelFeatureTreeMixin.Mutator.super.addFeatureBelow(newFeature, parentFeature, index);
            getMutable().getFeatureCache().add(newFeature);
            getMutable().getElementCache().put(newFeature.getIdentifier(), newFeature);
        }

        @Override
        default void removeFeature(Feature feature) {
            FeatureModelFeatureTreeMixin.Mutator.super.removeFeature(feature);
            getMutable().getFeatureCache().remove(feature);
            getMutable().getElementCache().remove(feature.getIdentifier());
        }

        @Override
        default void setConstraint(int index, Constraint constraint) {
            getMutable()
                    .getElementCache()
                    .remove(getMutable().getConstraints().get(index).getIdentifier());
            FeatureModelConstraintMixin.Mutator.super.setConstraint(index, constraint);
            getMutable().getElementCache().put(constraint.getIdentifier(), constraint);
        }

        @Override
        default void addConstraint(Constraint newConstraint, int index) {
            FeatureModelConstraintMixin.Mutator.super.addConstraint(newConstraint, index);
            getMutable().getElementCache().put(newConstraint.getIdentifier(), newConstraint);
        }

        @Override
        default void removeConstraint(Constraint constraint) {
            FeatureModelConstraintMixin.Mutator.super.removeConstraint(constraint);
            getMutable().getElementCache().remove(constraint.getIdentifier());
        }

        @Override
        default Constraint removeConstraint(int index) {
            Constraint constraint = FeatureModelConstraintMixin.Mutator.super.removeConstraint(index);
            getMutable().getElementCache().remove(constraint.getIdentifier());
            return constraint;
        }
    }
}
