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

import de.featjar.base.data.*;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.IFormula;
import java.util.*;

public class Constraint extends AFeatureModelElement implements IConstraint {
    protected final IFeatureModel featureModel;
    protected IFormula formula;
    protected final LinkedHashSet<IFeature> containedFeaturesCache = Sets.empty();
    protected IConstraint.Mutator mutator;

    public Constraint(IFeatureModel featureModel) {
        super(featureModel.getNewIdentifier());
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
        this.formula = Expressions.True;
    }

    @Override
    public IFeatureModel getFeatureModel() {
        return featureModel;
    }

    @Override
    public IFormula getFormula() {
        return formula;
    }

    @Override
    public LinkedHashSet<IFeature> getReferencedFeatures() {
        return containedFeaturesCache;
    }

    @Override
    public IConstraint.Mutator getMutator() {
        return mutator == null ? (mutator = new Mutator()) : mutator;
    }

    @Override
    public void setMutator(IConstraint.Mutator mutator) {
        this.mutator = mutator;
    }

    @Override
    public String toString() {
        return String.format("Constraint{formula=%s}", formula);
    }

    public class Mutator implements IConstraint.Mutator {
        @Override
        public Constraint getMutable() {
            return Constraint.this;
        }

        @Override
        public void setFormula(IFormula formula) {
            containedFeaturesCache.clear();
            containedFeaturesCache.addAll(IConstraint.getReferencedFeatures(formula, featureModel));
            Constraint.this.formula = formula;
        }
    }
}
