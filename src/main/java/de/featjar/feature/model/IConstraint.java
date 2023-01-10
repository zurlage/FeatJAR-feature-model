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

import de.featjar.base.data.*;
import de.featjar.feature.model.mixins.IHasCommonAttributes;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.term.value.Variable;
import java.util.LinkedHashSet;

/**
 * A constraint describes some restriction on the valid configurations represented by a {@link FeatureModel}.
 * It is attached to a {@link FeatureModel} and represented as a {@link IFormula} over {@link Feature} variables.
 * For safe mutation, rely only on the methods of {@link IMutable}.
 *
 * @author Elias Kuiter
 */
public interface IConstraint
        extends IFeatureModelElement, IHasCommonAttributes, IMutable<IConstraint, IConstraint.Mutator> {

    IFormula getFormula();

    static LinkedHashSet<IFeature> getReferencedFeatures(IFormula formula, IFeatureModel featureModel) {
        return formula.getVariableStream()
                .map(Variable::getName)
                .map(featureModel.getIdentifier().getFactory()::parse)
                .map(identifier -> {
                    Result<IFeature> feature = featureModel.getFeature(identifier);
                    if (feature.isEmpty())
                        throw new RuntimeException("encountered unknown feature identifier " + identifier);
                    return feature.get();
                })
                .collect(Sets.toSet());
    }

    default LinkedHashSet<IFeature> getReferencedFeatures() {
        return getReferencedFeatures(getFormula(), getFeatureModel());
    }

    @SuppressWarnings({"unchecked"})
    default LinkedHashSet<String> getTags() {
        return (LinkedHashSet<String>) getAttributeValue(Attributes.TAGS).get();
    }

    interface Mutator extends IMutator<IConstraint>, IHasCommonAttributes.Mutator<IConstraint> {
        void setFormula(IFormula formula);

        default void remove() {
            getMutable().getFeatureModel().mutate().removeConstraint(getMutable());
        }

        default void setTags(LinkedHashSet<String> tags) {
            setAttributeValue(Attributes.TAGS, tags);
        }

        default boolean addTag(String tag) {
            return getMutable().getTags().add(tag);
        }

        default boolean removeTag(String tag) {
            return getMutable().getTags().remove(tag);
        }
    }
}
