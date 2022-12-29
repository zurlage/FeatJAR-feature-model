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

import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.base.tree.structure.ATree;

import java.util.Objects;
import java.util.function.Predicate;

public class FeatureModelTree extends ATree<FeatureModelTree> {
    protected final FeatureModel featureModel;

    public FeatureModelTree(FeatureModel featureModel) {
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    /**
     * {@return a validator that guarantees that the root of a child is a leaf in its parent}
     */
    @Override
    public Predicate<FeatureModelTree> getChildrenValidator() {
        return featureModelTree -> {
            Result<Feature> featureInParent =
                    getFeatureModel().getFeature(featureModelTree.getFeatureModel().getRootFeature().getIdentifier());
            return featureInParent.isPresent() && !featureInParent.get().getFeatureTree().hasChildren();
        };
    }

    @Override
    public ITree<FeatureModelTree> cloneNode() {
        throw new RuntimeException();
    }

    @Override
    public boolean equalsNode(FeatureModelTree other) {
        throw new RuntimeException();
    }

    // TODO hashcode, equals, tostring, clone
}
