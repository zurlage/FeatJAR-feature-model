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

import de.featjar.base.data.*;
import de.featjar.base.data.identifier.AIdentifier;
import de.featjar.base.data.identifier.IIdentifier;
import de.featjar.feature.model.*;
import de.featjar.base.tree.Trees;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Implements a {@link IFeatureModel} mixin for common operations on the {@link IFeatureTree}.
 *
 * @author Elias Kuiter
 */
public interface IHasFeatureTree {
    IFeatureTree getFeatureTree();

    default LinkedHashSet<IFeature> getFeatures() {
        return Trees.preOrderStream(getFeatureTree())
                .map(IFeatureTree::getFeature)
                .collect(Sets.toSet());
    }

    default int getNumberOfFeatures() {
        return getFeatures().size();
    }

    default IFeature getRootFeature() {
        return getFeatureTree().getRoot().getFeature();
    }

    default Result<IFeature> getFeature(IIdentifier identifier) {
        Objects.requireNonNull(identifier);
        return Result.ofOptional(
                getFeatures().stream()
                        .filter(feature -> feature.getIdentifier().equals(identifier))
                        .findFirst());
    }

    default Result<IFeature> getFeature(String name) {
        Objects.requireNonNull(name);
        return Result.ofOptional(
                getFeatures().stream()
                        .filter(feature -> feature.getName().equals(name))
                        .findFirst());
    }

    default boolean hasFeature(IIdentifier identifier) {
        return getFeature(identifier).isPresent();
    }

    default boolean hasFeature(IFeature feature) {
        return hasFeature(feature.getIdentifier());
    }

    interface Mutator extends IMutator<IFeatureModel> {
        IFeature newFeature();

        default void addFeatureBelow(IFeature newFeature, IFeature parentFeature, int index) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(parentFeature);
            if (getMutable().hasFeature(newFeature) || !getMutable().hasFeature(parentFeature)) {
                throw new IllegalArgumentException();
            }
            parentFeature.getFeatureTree().addChild(index, newFeature.getFeatureTree());
        }

        default void addFeatureBelow(IFeature newFeature, IFeature parentFeature) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(parentFeature);
            addFeatureBelow(
                    newFeature, parentFeature, parentFeature.getFeatureTree().getChildrenCount());
        }

        default void addFeatureNextTo(IFeature newFeature, IFeature siblingFeature) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(siblingFeature);
            if (!siblingFeature.getFeatureTree().hasParent() || !getMutable().hasFeature(siblingFeature)) {
                throw new IllegalArgumentException();
            }
            addFeatureBelow(
                    newFeature,
                    siblingFeature.getFeatureTree().getParent().get().getFeature(),
                    siblingFeature.getFeatureTree().getIndex().get() + 1);
        }

        default IFeature createFeatureBelow(IFeature parentFeature, int index) {
            IFeature newFeature = newFeature();
            addFeatureBelow(newFeature, parentFeature, index);
            return newFeature;
        }

        default IFeature createFeatureBelow(IFeature parentFeature) {
            IFeature newFeature = newFeature();
            addFeatureBelow(newFeature, parentFeature);
            return newFeature;
        }

        default IFeature createFeatureNextTo(IFeature siblingFeature) {
            IFeature newFeature = newFeature();
            addFeatureNextTo(newFeature, siblingFeature);
            return newFeature;
        }

        default void removeFeature(IFeature feature) { // TODO what about the containing constraints?
            Objects.requireNonNull(feature);
            if (feature.equals(getMutable().getRootFeature()) || !getMutable().hasFeature(feature)) {
                throw new IllegalArgumentException();
            }

            final IFeatureTree parentFeatureTree = feature.getFeatureTree().getParent().get();

            if (parentFeatureTree.getChildrenCount() == 1) {
                parentFeatureTree.mutate(mutator -> {
                    if (feature.getFeatureTree().isAnd()) {
                        mutator.setAnd();
                    } else if (feature.getFeatureTree().isAlternative()) {
                        mutator.setAlternative();
                    } else {
                        mutator.setOr();
                    }
                });
            }

            final int index = feature.getFeatureTree().getIndex().get();
            while (feature.getFeatureTree().hasChildren()) {
                parentFeatureTree.addChild(
                        index,
                        feature.getFeatureTree()
                                .removeChild(feature.getFeatureTree().getChildrenCount() - 1));
            }

            parentFeatureTree.removeChild(feature.getFeatureTree());
        }
    }
}
