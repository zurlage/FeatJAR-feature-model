package de.featjar.feature.model.order;

import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.mixins.IHasFeatureTree;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ComparatorFeatureOrder extends AFeatureOrder {
    protected final Comparator<IFeature> featureComparator;

    public ComparatorFeatureOrder(Comparator<IFeature> featureComparator) {
        this.featureComparator = featureComparator;
    }

    @Override
    public List<IFeature> apply(IHasFeatureTree featureModel) {
        return featureModel.getFeatures().stream().sorted(featureComparator).collect(Collectors.toList());
    }
}
