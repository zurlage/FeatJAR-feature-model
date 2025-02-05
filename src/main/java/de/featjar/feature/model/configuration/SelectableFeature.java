/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
 * See <https://github.com/FeatureIDE/FeatJAR-feature-model> for further information.
 */
package de.featjar.feature.model.configuration;

import de.featjar.base.FeatJAR;
import de.featjar.feature.model.IFeature;
import java.util.*;

/**
 * A representation of a selectable feature for the configuration process.
 *
 * @author Marcus Pinnecke (Feature Interface)
 * @author Luca zur Lage
 * @author Werner Münch
 * @author Tom Röhrig
 */
public class SelectableFeature {

    private Selection manual = Selection.UNDEFINED;

    private Selection automatic = Selection.UNDEFINED;

    private IFeature feature;

    private String name;

    public SelectableFeature(String name) {
        this.name = name;
    }

    public SelectableFeature(IFeature feature) {
        this.feature = feature;
    }

    public SelectableFeature(SelectableFeature oldSelectableFeature) {
        feature = oldSelectableFeature.feature;
        name = oldSelectableFeature.name;
        manual = oldSelectableFeature.manual;
        automatic = oldSelectableFeature.automatic;
    }

    public Selection getSelection() {
        return automatic == Selection.UNDEFINED ? manual : automatic;
    }

    public Selection getManual() {
        return manual;
    }

    public void setManual(Selection manual) {
        if ((manual == Selection.UNDEFINED) || (automatic == Selection.UNDEFINED)) {
            this.manual = manual;
        } else if (manual != automatic) {
            throw new SelectionNotPossibleException(getName(), manual);
        }
    }

    public Selection getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Selection automatic) {
        if ((automatic == Selection.UNDEFINED) || (manual == Selection.UNDEFINED) || (manual == automatic)) {
            this.automatic = automatic;
        } else {
            throw new AutomaticalSelectionNotPossibleException(getName(), automatic);
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return feature == null ? "" : feature.getName().get();
    }

    public void setFeature(IFeature feature) {
        this.feature = feature;
    }

    public IFeature getFeature() {
        return feature;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates and returns a copy of this selectable feature.
     *
     * @return selectableFeature a clone of this selectable feature
     */
    @Override
    public SelectableFeature clone() {
        if (!this.getClass().equals(SelectableFeature.class)) {
            try {
                return (SelectableFeature) super.clone();
            } catch (final CloneNotSupportedException e) {
                FeatJAR.log().error(e);
                throw new RuntimeException("Cloning is not supported for " + this.getClass());
            }
        }
        return new SelectableFeature(this);
    }

    // TODO genutzt in Configuration.java
    public void cloneProperties(SelectableFeature feat) {}
}
