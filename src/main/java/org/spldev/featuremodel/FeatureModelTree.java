package org.spldev.featuremodel;

import org.spldev.util.tree.structure.RootedTree;
import org.spldev.util.tree.structure.Tree;

import java.util.Objects;

public class FeatureModelTree extends RootedTree<FeatureModelTree> {
    /**
     * Feature model at the root of this feature model tree.
     */
    protected final PureFeatureModel featureModel;

    public FeatureModelTree(PureFeatureModel featureModel) {
        Objects.requireNonNull(featureModel);
        this.featureModel = featureModel;
    }

    public PureFeatureModel getFeatureModel() {
        return featureModel;
    }

    public boolean isRoot() {
        return !hasParent();
    }

    public boolean isFlat() {
        return isRoot() && !hasChildren();
    }

    @Override
    public Tree<FeatureModelTree> cloneNode() {
        throw new RuntimeException();
    }
}
