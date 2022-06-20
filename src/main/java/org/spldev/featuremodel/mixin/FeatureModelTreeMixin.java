package org.spldev.featuremodel.mixin;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModelTree;
import org.spldev.featuremodel.Identifier;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.util.tree.Trees;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface FeatureModelTreeMixin {
    FeatureModelTree getFeatureModelTree();

    default Set<FeatureModel> getFeatureModels() {
        return Trees.parallelStream(getFeatureModelTree()).map(FeatureModelTree::getFeatureModel).collect(Collectors.toSet());
    }

    default int getNumberOfFeatureModels() {
        return getFeatureModels().size();
    }

    default Optional<FeatureModel> getFeatureModel(Identifier<?> identifier) {
        Objects.requireNonNull(identifier);
        return getFeatureModels().stream().filter(featureModel -> featureModel.getIdentifier().equals(identifier)).findFirst();
    }

    default boolean hasFeatureModel(Identifier<?> identifier) {
        return getFeatureModel(identifier).isPresent();
    }

    default boolean hasFeatureModel(Feature feature) {
        return hasFeatureModel(feature.getIdentifier());
    }
}
