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
package de.featjar.feature.model.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.feature.model.FeatureModel;
import de.featjar.feature.model.io.xml.GraphVizFeatureModelFormat;
import de.featjar.feature.model.io.xml.XMLFeatureModelFormat;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class GraphVizFeatureModelFormatTest {
    @Test
    public void graphVizFeatureModelFormat() throws IOException {
        Result<FeatureModel> featureModel =
                IO.load(Paths.get("src/test/resources/testFeatureModels/car.xml"), new XMLFeatureModelFormat());
        assertTrue(featureModel.isPresent());
        assertTrue(
                IO.print(featureModel.get(), new GraphVizFeatureModelFormat()).startsWith("digraph {"));
    }
}
