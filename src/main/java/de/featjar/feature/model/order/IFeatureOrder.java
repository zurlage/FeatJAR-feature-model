package de.featjar.feature.model.order;

import de.featjar.base.data.IMutable;
import de.featjar.base.data.IMutator;
import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.mixins.IHasFeatureTree;
import java.util.List;
import java.util.function.Function;

/**
 * Orders features in different ways.
 *
 * @author Elias Kuiter
 */
public interface IFeatureOrder
        extends Function<IHasFeatureTree, List<IFeature>>, IMutable<IFeatureOrder, IFeatureOrder.Mutator> {
    boolean isUserDefined();

    interface Mutator extends IMutator<IFeatureOrder> {
        void setUserDefined(boolean userDefined);
    }
}
