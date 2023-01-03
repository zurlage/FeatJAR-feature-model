package de.featjar.feature.model;

import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;

import java.util.function.Predicate;

public interface IFeatureModelTree extends ITree<IFeatureModelTree> {
    IFeatureModel getFeatureModel();

    /**
     * {@return a validator that guarantees that the root of a child is a leaf in its parent}
     */
    @Override
    default Predicate<IFeatureModelTree> getChildValidator() {
        return featureModelTree -> {
            // todo
            Result<IFeature> featureInParent =
                    getFeatureModel().getFeature(featureModelTree.getFeatureModel().getRootFeature().getIdentifier());
            return featureInParent.isPresent() && !featureInParent.get().getFeatureTree().hasChildren();
        };
    }
}
