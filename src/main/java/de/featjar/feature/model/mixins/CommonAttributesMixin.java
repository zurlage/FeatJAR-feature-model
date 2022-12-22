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

import de.featjar.feature.model.Attributes;
import de.featjar.feature.model.Constraint;
import de.featjar.feature.model.Feature;
import de.featjar.feature.model.FeatureModel;
import de.featjar.base.data.IAttributable;
import de.featjar.base.data.Attribute;
import java.util.Optional;

/**
 * Implements accessors for commonly used {@link Attribute attributes}.
 * For example, all {@link FeatureModel feature models}, {@link Feature features},
 * and {@link Constraint constraints} can have names and descriptions.
 *
 * @author Elias Kuiter
 */
public interface CommonAttributesMixin extends IAttributable {
    default String getName() {
        return (String) getAttributeValue(Attributes.NAME);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<String> getDescription() {
        return (Optional) getAttributeValue(Attributes.DESCRIPTION);
    }

    interface Mutator<T extends IAttributable> extends IAttributable.Mutator<T> {
        default void setName(String name) {
            setAttributeValue(Attributes.NAME, name);
        }

        default void setDescription(String description) {
            setAttributeValue(Attributes.DESCRIPTION, description);
        }
    }
}
