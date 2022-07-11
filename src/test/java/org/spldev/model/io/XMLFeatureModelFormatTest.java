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
package org.spldev.model.io;

import org.junit.jupiter.api.Test;
import org.spldev.model.Feature;
import org.spldev.model.FeatureModel;
import org.spldev.model.io.xml.XMLFeatureModelFormat;
import org.spldev.util.data.Result;
import org.spldev.util.io.IO;

import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMLFeatureModelFormatTest {
	@Test
	public void xmlFeatureModelFormat() {
		Result<FeatureModel> featureModelResult = IO.load(Paths.get(
			"src/test/resources/testFeatureModels/car.xml"), new XMLFeatureModelFormat());
		assertTrue(featureModelResult.isPresent());
		FeatureModel featureModel = featureModelResult.get();
		String[] featureNames = new String[] { "Car", "Carbody", "Radio", "Ports", "USB", "CD", "Navigation",
			"DigitalCards", "Europe", "USA", "GPSAntenna", "Bluetooth", "Gearbox", "Manual", "Automatic",
			"GearboxTest" };
		assertEquals(Set.of(featureNames), featureModel.getFeatures().stream().map(Feature::getName).collect(Collectors
			.toSet()));
	}
}
