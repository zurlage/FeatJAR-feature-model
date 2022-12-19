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

import de.featjar.base.data.Attributable;
import de.featjar.base.data.Attribute;
import de.featjar.base.data.Identifiable;
import de.featjar.base.data.Identifier;
import java.util.*;

/**
 * Implements identification and attribute valuation.
 * Each {@link FeatureModel} and all its {@link Feature features} and {@link Constraint constraints} are
 * uniquely identified by an {@link Identifier}.
 * Also, each element can be annotated with arbitrary {@link Attribute attributes}.
 *
 * @author Elias Kuiter
 */
public abstract class Element implements Identifiable, Attributable {
    protected final Identifier identifier;
    protected final LinkedHashMap<Attribute, Object> attributeToValueMap = new LinkedHashMap<>();

    public Element(Identifier identifier) {
        Objects.requireNonNull(identifier);
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public abstract FeatureModel getFeatureModel();

    @Override
    public LinkedHashMap<Attribute, Object> getAttributeToValueMap() {
        return attributeToValueMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element that = (Element) o;
        return getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}
