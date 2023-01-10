/*
 * Copyright (C) 2022 Elias Kuiter
 *
 * This file is part of model.
 *
 * model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model.mixins;

import de.featjar.feature.model.*;
import de.featjar.feature.model.order.IFeatureOrder;
import java.util.List;

/**
 * Implements a {@link IFeatureModel} mixin for considering a {@link IFeatureOrder}.
 *
 * @author Elias Kuiter
 */
public interface IHasFeatureOrder extends IHasFeatureTree {
    IFeatureOrder getFeatureOrder();

    default List<IFeature> getOrderedFeatures() {
        return getFeatureOrder().apply(this);
    }

    interface Mutator {
        void setFeatureOrder(IFeatureOrder featureOrder);
    }
}
