/*
 * Copyright (C) 2023 Elias Kuiter
 *
 * This file is part of FeatJAR-feature-model.
 *
 * feature-model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * feature-model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with feature-model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import de.featjar.base.data.Attribute;
import de.featjar.base.data.IAttribute;
import de.featjar.base.data.Maps;
import de.featjar.base.data.identifier.AIdentifier;
import de.featjar.base.data.identifier.IIdentifier;
import java.util.*;

/**
 * Implements identification and attribute valuation.
 * Each {@link FeatureModel} and all its {@link Feature features} and {@link Constraint constraints} are
 * uniquely identified by some {@link AIdentifier}.
 * Also, each element can be annotated with arbitrary {@link Attribute attributes}.
 *
 * @author Elias Kuiter
 */
public abstract class AFeatureModelElement implements IFeatureModelElement {
    protected final IIdentifier identifier;
    protected final LinkedHashMap<IAttribute, Object> attributeValues = Maps.empty();

    public AFeatureModelElement(IIdentifier identifier) {
        Objects.requireNonNull(identifier);
        this.identifier = identifier;
    }

    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public LinkedHashMap<IAttribute, Object> getAttributeValues() {
        return attributeValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AFeatureModelElement that = (AFeatureModelElement) o;
        return getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}
