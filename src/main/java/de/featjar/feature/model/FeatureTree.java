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

import de.featjar.base.data.Range;
import de.featjar.base.tree.structure.ARootedTree;
import de.featjar.base.tree.structure.ITree;

import java.util.Objects;

public class FeatureTree extends ARootedTree<IFeatureTree> implements IFeatureTree {
    /**
     * Feature at the root of this feature tree.
     */
    protected final IFeature feature;

    /**
     * Whether this tree's feature is mandatory or optional.
     */
    protected boolean isMandatory;

    /**
     * Range of how many child features may be selected.
     */
    // TODO: use attribute (and move to Feature class, or merge Feature+FeatureTree)? add dynamic attributes "isSelected/automatic"?
    protected Range groupRange = Range.open();

    protected IFeatureTree.Mutator mutator;

    public FeatureTree(IFeature feature) {
        Objects.requireNonNull(feature);
        this.feature = feature;
    }

    @Override
    public IFeature getFeature() {
        return feature;
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public Range getGroupRange() {
        return groupRange;
    }

    @Override
    public ITree<IFeatureTree> cloneNode() {
        throw new RuntimeException();
    }

    @Override
    public boolean equalsNode(IFeatureTree other) {
        return false; // todo
    }

    @Override
    public int hashCodeNode() {
        return 0; //todo
    }

    @Override
    public IFeatureTree.Mutator getMutator() {
        return mutator == null ? (mutator = new Mutator()) : mutator;
    }

    @Override
    public void setMutator(IFeatureTree.Mutator mutator) {
        this.mutator = mutator;
    }

    // TODO hashcode, equals, tostring, clone

    public class Mutator implements IFeatureTree.Mutator {
        @Override
        public FeatureTree getMutable() {
            return FeatureTree.this;
        }

        public void setMandatory(boolean isMandatory) {
            FeatureTree.this.isMandatory = isMandatory;
        }

        public void setGroupRange(Range groupRange) {
            FeatureTree.this.groupRange = groupRange;
        }
    }
}
