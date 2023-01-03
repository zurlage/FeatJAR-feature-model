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
public interface IFeatureTree extends
        IRootedTree<IFeatureTree>,
        IMutable<IFeatureTree, IFeatureTree.Mutator> {
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
