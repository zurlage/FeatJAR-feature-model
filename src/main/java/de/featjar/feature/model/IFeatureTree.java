/*
 * Copyright (C) 2023 Elias Kuiter
 *
 * This file is part of FeatJAR-feature-model.
 *
 * feature-model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * feature-model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with feature-model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import de.featjar.base.data.IMutable;
import de.featjar.base.data.IMutator;
import de.featjar.base.data.Range;
import de.featjar.base.tree.structure.ARootedTree;
import de.featjar.base.tree.structure.IRootedTree;

/**
 * An ordered {@link ARootedTree} labeled with {@link Feature features}.
 * Implements some concepts from feature-oriented domain analysis, such as mandatory/optional features and groups.
 *
 * @author Elias Kuiter
 */
public interface IFeatureTree extends IRootedTree<IFeatureTree>, IMutable<IFeatureTree, IFeatureTree.Mutator> {
    IFeature getFeature();

    boolean isMandatory();

    default boolean isOptional() {
        return !isMandatory();
    }

    Range getGroupRange();

    default boolean isGroupRange(Range other) {
        return getGroupRange().equals(other);
    }

    default boolean isAnd() {
        return isGroupRange(Range.open());
    }

    default boolean isAlternative() {
        return isGroupRange(Range.exactly(1));
    }

    default boolean isOr() {
        return isGroupRange(Range.atLeast(1));
    }

    default boolean isGroup() {
        return !isAnd();
    }

    interface Mutator extends IMutator<IFeatureTree> {
        void setMandatory(boolean isMandatory);

        default boolean toggleMandatory() {
            boolean isMandatory = getMutable().isMandatory();
            setMandatory(!isMandatory);
            return !isMandatory;
        }

        default void setOptional() {
            setMandatory(false);
        }

        default void setMandatory() {
            setMandatory(true);
        }

        void setGroupRange(Range groupRange);

        default void setAnd() {
            setGroupRange(Range.open());
        }

        default void setAlternative() {
            setGroupRange(Range.exactly(1));
        }

        default void setOr() {
            setGroupRange(Range.atLeast(1));
        }
    }
}
