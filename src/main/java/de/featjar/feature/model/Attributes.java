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
package de.featjar.feature.model;

import de.featjar.base.data.Attribute;
import de.featjar.base.data.Sets;
import de.featjar.base.data.identifier.IIdentifiable;

import java.util.LinkedHashSet;

/**
 * Defines useful {@link Attribute attributes} for {@link FeatureModel feature models},
 * {@link Feature features}, and {@link Constraint constraints}.
 *
 * @author Elias Kuiter
 */
public class Attributes {
    public static final String NAMESPACE = Attributes.class.getCanonicalName();

    public static final Attribute NAME = new Attribute(NAMESPACE, "name", String.class)
            .setDefaultValueFunction(identifiable ->
                    "@" + ((IIdentifiable) identifiable).getIdentifier().toString())
            .setValidator((element, name) -> // todo: can also be name of feature model or constraint, but this validates only feature name uniqueness
                    ((AFeatureModelElement) element).getFeatureModel().getFeature((String) name).isEmpty());

    public static final Attribute DESCRIPTION = new Attribute(NAMESPACE, "description", String.class);

    public static final Attribute TAGS =
            new Attribute(NAMESPACE, "tags", LinkedHashSet.class)
                    .setDefaultValueFunction(attributable -> Sets.empty());

    public static final Attribute HIDDEN =
            new Attribute(NAMESPACE, "hidden", Boolean.class)
                    .setDefaultValue(false);

    public static final Attribute ABSTRACT =
            new Attribute(NAMESPACE, "abstract", Boolean.class)
                    .setDefaultValue(false);
}
