package org.spldev.featuremodel;

import org.spldev.featuremodel.mixins.FeatureModelFeatureTreeMixin;
import org.spldev.util.tree.Trees;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Orders features in different ways.
 * By default, the feature-tree preorder is used.
 *
 * @author Elias Kuiter
 */
public interface FeatureOrder extends Function<FeatureModelFeatureTreeMixin, List<Feature>> {
    static FeatureOrder ofPreOrder() {
        return featureModel -> Trees.preOrderStream(featureModel.getFeatureTree())
                .map(FeatureTree::getFeature)
                .collect(Collectors.toList());
    }

    static FeatureOrder ofComparator(Comparator<Feature> featureComparator) {
        return featureModel -> featureModel.getFeatures().stream()
                .sorted(featureComparator)
                .collect(Collectors.toList());
    }

    static FeatureOrder ofList(List<Feature> featureList) {
        return featureModel -> Stream.concat(featureList.stream().filter(featureModel.getFeatures()::contains),
                        featureModel.getFeatures().stream().filter(feature -> !featureList.contains(feature)))
                .collect(Collectors.toList());
    }
}
