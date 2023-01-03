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
public interface IConstraint extends
        IFeatureModelElement,
        IHasCommonAttributes,
        IMutable<IConstraint, IConstraint.Mutator> {

    IFormula getFormula();

    static LinkedHashSet<IFeature> getReferencedFeatures(IFormula formula, IFeatureModel featureModel) {
        return formula
                .getVariableStream()
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

    interface Mutator extends
            IMutator<IConstraint>,
            IHasCommonAttributes.Mutator<IConstraint> {
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
