package de.featjar.feature.model.order;

import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.mixins.IHasFeatureTree;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListFeatureOrder extends AFeatureOrder {
    protected final List<IFeature> featureList;

    public ListFeatureOrder(List<IFeature> featureList) {
        this.featureList = featureList;
    }

    @Override
    public List<IFeature> apply(IHasFeatureTree featureModel) {
        return Stream.concat(
                        featureList.stream().filter(featureModel.getFeatures()::contains),
                        featureModel.getFeatures().stream().filter(feature -> !featureList.contains(feature)))
                .collect(Collectors.toList());
    }
}
