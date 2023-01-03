package de.featjar.feature.model;

import de.featjar.base.data.IMutable;
import de.featjar.base.data.IMutator;
import de.featjar.feature.model.mixins.*;

/**
 * A feature model represents the configuration space of a software system.
 * We equate feature models with feature diagrams
 * (i.e., a {@link FeatureTree} labeled with features and a list of {@link Constraint constraints}).
 * For safe mutation, rely only on the methods of {@link IMutable}.
 *
 * cache assumes that features/constraints are only added/deleted through the mutator, not manually
 *
 * @author Elias Kuiter
 */
public interface IFeatureModel extends
        IFeatureModelElement,
        IHasCommonAttributes,
        IHasFeatureTree,
        IHasConstraints,
        IHasFeatureOrder,
        //IHasElementCache, //todo: ?
        IMutable<IFeatureModel, IFeatureModel.Mutator> {
    //TODO put flattened fm into store (maybe dispatch mutators of flattened model to original models)

    //TODO: we allow all kinds of modeling constructs, but not all analyses/computations support all constructs.
    //e.g., multiplicities are difficult to map to SAT. somehow, this should be checked.
    // maybe store required/incompatible capabilities for computations? eg., incompatible with Plaisted-Greenbaum/multiplicities/...?
    //and then implement different alternative algorithms with different capabilities.
    //maybe this could be encoded first-class as a feature model.
    //this could even be used to generate query plans (e.g., find some configuration that counts my formula).
    //every plugin defines a feature model (uvl) that restricts what its extensions can and cannot do (replacing extensions.xml)

    FeatureModelTree getFeatureModelTree();

    interface Mutator extends
            IMutator<IFeatureModel>,
            IHasFeatureTree.Mutator,
            IHasConstraints.Mutator,
            IHasFeatureOrder.Mutator,
            IHasCommonAttributes.Mutator<IFeatureModel> {
            //IHasElementCache.Mutator { todo
    }
}
