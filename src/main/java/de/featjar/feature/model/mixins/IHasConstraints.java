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
import de.featjar.base.data.identifier.IIdentifier;
import de.featjar.feature.model.IConstraint;
import de.featjar.feature.model.IFeatureModel;
import de.featjar.formula.structure.formula.IFormula;
import java.util.*;

/**
 * Implements a {@link IFeatureModel} mixin for common operations on {@link IConstraint constraints}.
 *
 * @author Elias Kuiter
 */
public interface IHasConstraints {
    List<IConstraint> getConstraints();

    default Result<IConstraint> getConstraint(IIdentifier identifier) {
        Objects.requireNonNull(identifier);
        return Result.ofOptional(getConstraints().stream()
                .filter(constraint -> constraint.getIdentifier().equals(identifier))
                .findFirst());
    }

    default boolean hasConstraint(IIdentifier identifier) {
        return getConstraint(identifier).isPresent();
    }

    default boolean hasConstraint(IConstraint constraint) {
        return hasConstraint(constraint.getIdentifier());
    }

    default Result<Integer> getConstraintIndex(IConstraint constraint) {
        Objects.requireNonNull(constraint);
        return Result.ofIndex(getConstraints().indexOf(constraint));
    }

    default int getNumberOfConstraints() {
        return getConstraints().size();
    }

    interface Mutator extends IMutator<IFeatureModel> {
        IConstraint newConstraint(); // todo: maybe mark methods like this that are internal APIs? (with _?)

        default void setConstraint(int index, IConstraint constraint) {
            Objects.requireNonNull(constraint);
            if (getMutable().hasConstraint(constraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().set(index, constraint);
        }

        default void setConstraints(Iterable<IConstraint> constraints) {
            Objects.requireNonNull(constraints);
            getMutable().getConstraints().clear();
            constraints.forEach(this::addConstraint);
        }

        default void addConstraint(IConstraint newConstraint, int index) {
            Objects.requireNonNull(newConstraint);
            if (getMutable().hasConstraint(newConstraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().add(index, newConstraint);
        }

        default void addConstraint(IConstraint newConstraint) {
            addConstraint(newConstraint, getMutable().getConstraints().size());
        }

        default IConstraint createConstraint(IFormula formula) {
            IConstraint newConstraint = newConstraint();
            newConstraint.mutate().setFormula(formula);
            addConstraint(newConstraint);
            return newConstraint;
        }

        default IConstraint createConstraint(IFormula formula, int index) {
            IConstraint newConstraint = newConstraint();
            newConstraint.mutate().setFormula(formula);
            addConstraint(newConstraint, index);
            return newConstraint;
        }

        default IConstraint createConstraint() {
            IConstraint newConstraint = newConstraint();
            addConstraint(newConstraint);
            return newConstraint;
        }

        default void removeConstraint(IConstraint constraint) {
            Objects.requireNonNull(constraint);
            if (!getMutable().hasConstraint(constraint)) {
                throw new IllegalArgumentException();
            }
            getMutable().getConstraints().remove(constraint);
        }

        default IConstraint removeConstraint(int index) {
            return getMutable().getConstraints().remove(index);
        }
    }
}
