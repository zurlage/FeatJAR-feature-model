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

import de.featjar.base.data.Mutable;
import de.featjar.base.data.Range;
import de.featjar.base.tree.structure.RootedTree;
import de.featjar.base.tree.structure.Traversable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An ordered {@link RootedTree} labeled with {@link Feature features}.
 * Implements some concepts from feature-oriented domain analysis, such as mandatory/optional features and groups.
 *
 * @author Elias Kuiter
 */
public class FeatureTree extends RootedTree<FeatureTree> implements Mutable<FeatureTree, FeatureTree.Mutator> {
    /**
     * Feature at the root of this feature tree.
     */
    protected final Feature feature;

    /**
     * Whether this tree's feature is mandatory or optional.
     */
    protected boolean isMandatory;

    /**
     * Range of how many child features may be selected.
     */
    // todo: use attribute (and move to Feature class, or merge Feature+FeatureTree)? add dynamic attributes "isSelected/automatic"?
    protected Range groupRange = Range.open();

    protected Mutator mutator;

    public FeatureTree(Feature feature) {
        Objects.requireNonNull(feature);
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public boolean isOptional() {
        return !isMandatory;
    }

    public Range getGroupRange() {
        return groupRange;
    }

    public boolean isGroupRange(Range other) {
        return groupRange.equals(other);
    }

    public boolean isAnd() {
        return isGroupRange(Range.open());
    }

    public boolean isAlternative() {
        return isGroupRange(Range.exactly(1));
    }

    public boolean isOr() {
        return isGroupRange(Range.atLeast(1));
    }

    public boolean isGroup() {
        return !isAnd();
    }

    public Set<Constraint> getContainingConstraints() {
        return feature.getFeatureModel().getConstraints().stream()
                .filter(constraint -> constraint.getContainedFeatures().stream().anyMatch(feature::equals))
                .collect(Collectors.toSet());
    }

    @Override
    public Traversable<FeatureTree> cloneNode() {
        throw new RuntimeException();
    }

    @Override
    public boolean equalsNode(FeatureTree other) {
        throw new RuntimeException();
    }

    @Override
    public Mutator getMutator() {
        return mutator == null ? (mutator = new Mutator()) : mutator;
    }

    @Override
    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
    }

    // todo hashcode, equals, tostring, clone

    public class Mutator implements de.featjar.base.data.Mutator<FeatureTree> {
        @Override
        public FeatureTree getMutable() {
            return FeatureTree.this;
        }

        public void setOptional() {
            isMandatory = false;
        }

        public void setMandatory() {
            isMandatory = true;
        }

        //public boolean toggleMandatory() { todo
            //return toggleAttributeValue(Attributes....);;
        //}

        public void setGroupRange(Range groupRange) {
            FeatureTree.this.groupRange = groupRange;
        }

        public void setAnd() {
            setGroupRange(Range.open());
        }

        public void setAlternative() {
            setGroupRange(Range.exactly(1));
        }

        public void setOr() {
            setGroupRange(Range.atLeast(1));
        }
    }
}
