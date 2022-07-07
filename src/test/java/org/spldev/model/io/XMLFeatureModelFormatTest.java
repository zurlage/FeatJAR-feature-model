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
