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

import de.featjar.base.tree.structure.ATree;
import de.featjar.base.tree.structure.ITree;
import java.util.Objects;

public class FeatureModelTree extends ATree<IFeatureModelTree> implements IFeatureModelTree {
    protected final FeatureModel featureModel;

    public FeatureModelTree(FeatureModel featureModel) {
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
    }

    @Override
    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    @Override
    public ITree<IFeatureModelTree> cloneNode() {
        throw new RuntimeException();
    }

    @Override
    public boolean equalsNode(IFeatureModelTree other) {
        throw new RuntimeException();
    }

    @Override
    public int hashCodeNode() {
        throw new RuntimeException();
    }

    // TODO hashcode, equals, tostring, clone
}
