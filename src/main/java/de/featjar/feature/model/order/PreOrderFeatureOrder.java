package de.featjar.feature.model.order;

import de.featjar.base.tree.Trees;
import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.IFeatureTree;
import de.featjar.feature.model.mixins.IHasFeatureTree;

import java.util.List;
import java.util.stream.Collectors;

public class PreOrderFeatureOrder extends AFeatureOrder {
    @Override
    public List<IFeature> apply(IHasFeatureTree featureModel) {
        return Trees.preOrderStream(featureModel.getFeatureTree())
                .map(IFeatureTree::getFeature)
                .collect(Collectors.toList());
    }
}
