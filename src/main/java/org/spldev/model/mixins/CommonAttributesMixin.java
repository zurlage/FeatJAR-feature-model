/* -----------------------------------------------------------------------------
 * model - Feature models and configurations
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
 * See <https://github.com/FeatJAR/model> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.model.mixins;

import org.spldev.model.util.Attribute;
import org.spldev.model.Attributes;
import org.spldev.model.util.Attributable;

import java.util.Optional;

/**
 * Implements accessors for commonly used {@link Attribute attributes}. For
 * example, all {@link org.spldev.model.FeatureModel feature models},
 * {@link org.spldev.model.Feature features}, and
 * {@link org.spldev.model.Constraint constraints} can have names and
 * descriptions.
 *
 * @author Elias Kuiter
 */
public interface CommonAttributesMixin extends Attributable {
	default String getName() {
		return getAttributeValue(Attributes.NAME);
	}

	default Optional<String> getDescription() {
		return getAttributeValue(Attributes.DESCRIPTION);
	}

	interface Mutator<T extends Attributable> extends Attributable.Mutator<T> {
		default void setName(String name) {
			setAttributeValue(Attributes.NAME, name);
		}

		default void setDescription(String description) {
			setAttributeValue(Attributes.DESCRIPTION, description);
		}
	}
}
