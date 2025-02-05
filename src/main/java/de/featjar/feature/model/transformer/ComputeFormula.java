/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
 * See <https://github.com/FeatureIDE/FeatJAR-feature-model> for further information.
 */
package de.featjar.feature.model.transformer;

import de.featjar.base.computation.AComputation;
import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.feature.model.FeatureTree.Group;
import de.featjar.feature.model.IConstraint;
import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.IFeatureModel;
import de.featjar.feature.model.IFeatureTree;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Between;
import de.featjar.formula.structure.connective.Choose;
import de.featjar.formula.structure.connective.Implies;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.connective.Reference;
import de.featjar.formula.structure.predicate.Literal;
import de.featjar.formula.structure.term.value.Variable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Transforms a feature model into a boolean formula.
 *
 * @author Sebastian Krieter
 */
public class ComputeFormula extends AComputation<IFormula> {
    protected static final Dependency<IFeatureModel> FEATURE_MODEL = Dependency.newDependency(IFeatureModel.class);

    public ComputeFormula(IComputation<IFeatureModel> formula) {
        super(formula);
    }

    protected ComputeFormula(ComputeFormula other) {
        super(other);
    }

    @Override
    public Result<IFormula> compute(List<Object> dependencyList, Progress progress) {
        IFeatureModel featureModel = FEATURE_MODEL.get(dependencyList);
        HashSet<IFeatureModel> featureModels = new HashSet<>();
        ArrayList<IFormula> constraints = new ArrayList<>();
        HashSet<Variable> variables = new HashSet<>();
        featureModel.getFeatureTreeStream().forEach(tree -> {
            // TODO use better error value
            IFeature feature = tree.getFeature();
            String featureName = feature.getName().orElse("");
            Variable variable = new Variable(featureName, feature.getType());
            variables.add(variable);

            // TODO take featureRanges into Account
            Result<IFeatureTree> potentialParentTree = tree.getParent();
            // TODO take FeatureModel format into account, currently specifically written for UVL and XML format
            if (potentialParentTree.isEmpty()) {
                constraints.add(Expressions.literal(featureName));
                /*if (tree.isMandatory()) {
                	constraints.add(Expressions.literal(featureName));
                }*/
            } else {
                IFeatureTree parentTree = potentialParentTree.get();
                Literal literal = Expressions.literal(featureName);
                Literal parentLiteral =
                        Expressions.literal(parentTree.getFeature().getName().orElse(""));
                constraints.add(new Implies(literal, parentLiteral));

                for (Group group : parentTree.getGroups()) {
                    if (!group.isAnd() && !tree.isMandatory()) {
                        List<IFormula> groupLiterals = new ArrayList<>();
                        for (IFeatureTree childTree : parentTree.getChildren()) {
                            if (childTree.getGroup() == group) {
                                groupLiterals.add(Expressions.literal(
                                        childTree.getFeature().getName().orElse("")));
                            }
                        }
                        if (group.isOr()) {
                            Implies orGroup = new Implies(parentLiteral, new Or(groupLiterals));
                            if (!constraints.contains(orGroup)) {
                                constraints.add(orGroup);
                            }
                        } else if (group.isAlternative()) {
                            Implies alternativeGroup = new Implies(parentLiteral, new Choose(1, groupLiterals));
                            if (!constraints.contains(alternativeGroup)) {
                                constraints.add(alternativeGroup);
                            }
                        } else {
                            constraints.add(new Implies(
                                    parentLiteral,
                                    new Between(group.getLowerBound(), group.getUpperBound(), groupLiterals)));
                        }
                    } else {
                        if (tree.isMandatory()) {
                            Implies mandatoryImply = new Implies(parentLiteral, literal);
                            if (!constraints.contains(mandatoryImply)) {
                                constraints.add(mandatoryImply);
                            }
                        }
                    }
                }
            }
            for (IFeatureTree child : tree.getChildren()) {
                child.getGroup();
            }
            IFeatureModel featureModel2 = feature.getFeatureModel();
            if (featureModels.add(featureModel)) {
                featureModel2.getConstraints().stream()
                        .map(IConstraint::getFormula)
                        .forEach(constraints::add);
            }
        });
        Reference reference = new Reference(new And(constraints));
        reference.setFreeVariables(variables);
        return Result.of(reference);
    }
}
