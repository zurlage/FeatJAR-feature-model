package de.featjar.feature.model;

import de.featjar.base.data.IMutable;
import de.featjar.base.data.IMutator;
import de.featjar.base.data.Sets;
import de.featjar.base.data.identifier.AIdentifier;
import de.featjar.feature.model.mixins.IHasCommonAttributes;
import java.util.LinkedHashSet;

/**
 * A feature in a {@link FeatureModel} describes some functionality of a software system.
 * It is attached to a {@link FeatureModel} and labels a {@link FeatureTree}.
 * For safe mutation, rely only on the methods of {@link IMutable}.
 * A {@link Feature} is uniquely determined by its immutable {@link AIdentifier}
 * or name (obtained with {@link IHasCommonAttributes#getName()}).
 * In contrast to a feature's identifier, its name is mutable and should therefore be used sparsely
 * to avoid cache invalidation and renaming issues.
 *
 * @author Elias Kuiter
 */
public interface IFeature extends IFeatureModelElement, IHasCommonAttributes, IMutable<IFeature, IFeature.Mutator> {
    IFeatureTree getFeatureTree();

    default boolean isAbstract() {
        return (boolean) getAttributeValue(Attributes.ABSTRACT).get();
    }

    default boolean isConcrete() {
        return !isAbstract();
    }

    default boolean isHidden() {
        return (boolean) getAttributeValue(Attributes.HIDDEN).get();
    }

    default boolean isVisible() {
        return !isHidden();
    }

    default LinkedHashSet<IConstraint> getReferencingConstraints() {
        return getFeatureModel().getConstraints().stream()
                .filter(constraint ->
                        constraint.getReferencedFeatures().stream().anyMatch(this::equals))
                .collect(Sets.toSet());
    }

    interface Mutator extends IMutator<IFeature>, IHasCommonAttributes.Mutator<IFeature> {
        default void setAbstract(boolean value) {
            setAttributeValue(Attributes.ABSTRACT, value);
        }

        default boolean toggleAbstract() {
            return toggleAttributeValue(Attributes.ABSTRACT);
        }

        default void setAbstract() {
            setAbstract(true);
        }

        default void setConcrete() {
            setAbstract(false);
        }

        default void setHidden(boolean value) {
            setAttributeValue(Attributes.HIDDEN, value);
        }

        default boolean toggleHidden() {
            return toggleAttributeValue(Attributes.HIDDEN);
        }

        default void setHidden() {
            setHidden(true);
        }

        default void setVisible() {
            setHidden(false);
        }

        default void addFeatureBelow(IFeature newFeature, int index) {
            getMutable().getFeatureModel().mutate().addFeatureBelow(newFeature, getMutable(), index);
        }

        default void addFeatureBelow(IFeature newFeature) {
            getMutable().getFeatureModel().mutate().addFeatureBelow(newFeature, getMutable());
        }

        default void addFeatureNextTo(IFeature newFeature) {
            getMutable().getFeatureModel().mutate().addFeatureNextTo(newFeature, getMutable());
        }

        default IFeature createFeatureBelow(int index) {
            return getMutable().getFeatureModel().mutate().createFeatureBelow(getMutable(), index);
        }

        default IFeature createFeatureBelow() {
            return getMutable().getFeatureModel().mutate().createFeatureBelow(getMutable());
        }

        default IFeature createFeatureNextTo() {
            return getMutable().getFeatureModel().mutate().createFeatureNextTo(getMutable());
        }

        default void remove() {
            getMutable().getFeatureModel().mutate().removeFeature(getMutable());
        }
    }
}
