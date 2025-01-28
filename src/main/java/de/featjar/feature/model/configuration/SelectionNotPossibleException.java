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

/**
 * Identifies that the user has selected or deselected a feature which is not possible according the underlying feature model with current manual selections.
 *
 * @author Thomas Thuem
 */
public class SelectionNotPossibleException extends RuntimeException {

    private static final long serialVersionUID = 1793844229871267311L;

    public SelectionNotPossibleException(String feature, Selection selection) {
        super("The feature \"" + feature + "\" cannot be "
                + (selection == Selection.SELECTED ? "selected" : "deselected"));
    }
}
