package org.spldev.featuremodel;

import org.spldev.util.tree.structure.AbstractNonTerminal;
import org.spldev.util.tree.structure.Tree;

import java.util.Objects;

public class FeatureModelTree extends AbstractNonTerminal<FeatureModelTree> {
    /**
     * Feature model at the root of this feature model tree.
     */
    protected final FeatureModel featureModel;

    public FeatureModelTree(FeatureModel featureModel) {
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

//    public void mergeChild(FeatureModelTree child) {
//        // root of submodel must be leaf of parent model (or not contained -> auxiliary mandatory root)
//        // all non-roots of submodel must not occur in parent model
//        if (!hasChild(child)) {
//            throw new IllegalArgumentException();
//        }
//        child.getFeatureModel().getFeatures().forEach(feature -> {
//            if (feature.getFeatureTree().isRoot() && featureModel.getFeature(feature.getIdentifier())...)
//        });
//
//        child.getFeatureModel().getFeatureTree().setParent(...);
//
//        // todo add grandchild models
//        featureModel.invalidateCaches();
//    }

    @Override
    public Tree<FeatureModelTree> cloneNode() {
        throw new RuntimeException();
    }
}
