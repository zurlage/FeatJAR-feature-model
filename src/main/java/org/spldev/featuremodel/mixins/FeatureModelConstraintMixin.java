package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.Constraint;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.Identifier;
import org.spldev.formula.structure.Formula;
import org.spldev.util.data.Result;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface FeatureModelConstraintMixin {
    List<Constraint> getConstraints();

    default Optional<Constraint> getConstraint(Identifier<?> identifier) {
        Objects.requireNonNull(identifier);
        return getConstraints().stream().filter(constraint -> constraint.getIdentifier().equals(identifier)).findFirst();
    }

    default boolean hasConstraint(Identifier<?> identifier) {
        return getConstraint(identifier).isPresent();
    }

    default boolean hasConstraint(Constraint constraint) {
        return hasConstraint(constraint.getIdentifier());
    }

    default Optional<Integer> getConstraintIndex(Constraint constraint) {
        Objects.requireNonNull(constraint);
        return Result.indexToOptional(getConstraints().indexOf(constraint));
    }

    default int getNumberOfConstraints() {
        return getConstraints().size();
    }

    interface Mutator extends MutableMixin.Mutator<FeatureModel> {
        default void setConstraint(int index, Constraint constraint) {
            Objects.requireNonNull(constraint);
            if (getMutable().hasConstraint(constraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().set(index, constraint);
        }

        default void setConstraints(Iterable<Constraint> constraints) {
            Objects.requireNonNull(constraints);
            getMutable().getConstraints().clear();
            constraints.forEach(this::addConstraint);
        }

        default void addConstraint(Constraint newConstraint, int index) {
            Objects.requireNonNull(newConstraint);
            if (getMutable().hasConstraint(newConstraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().add(index, newConstraint);
        }

        default void addConstraint(Constraint newConstraint) {
            addConstraint(newConstraint, getMutable().getConstraints().size());
        }

        default Constraint createConstraint(Formula formula) {
            Constraint newConstraint = new Constraint(getMutable(), formula);
            addConstraint(newConstraint);
            return newConstraint;
        }

        default Constraint createConstraint(Formula formula, int index) {
            Constraint newConstraint = new Constraint(getMutable(), formula);
            addConstraint(newConstraint, index);
            return newConstraint;
        }

        default void removeConstraint(Constraint constraint) {
            Objects.requireNonNull(constraint);
            if (!getMutable().hasConstraint(constraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().remove(constraint);
        }

        default Constraint removeConstraint(int index) {
            return getMutable().getConstraints().remove(index);
        }
    }
}
