package org.spldev.featuremodel.io;

import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.Attributes;
import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.io.xml.XMLFeatureModelFormat;
import org.spldev.util.data.Result;
import org.spldev.util.io.FileHandler;

import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMLFeatureModelFormatTest {
	@Test
	public void xmlFeatureModelFormat() {
		Result<FeatureModel> featureModelResult = FileHandler.load(Paths.get(
			"src/test/resources/testFeatureModels/car.xml"), new XMLFeatureModelFormat());
		assertTrue(featureModelResult.isPresent());
		FeatureModel featureModel = featureModelResult.get();
		String[] featureNames = new String[] { "Car", "Carbody", "Radio", "Ports", "USB", "CD", "Navigation",
			"DigitalCards", "Europe", "USA", "GPSAntenna", "Bluetooth", "Gearbox", "Manual", "Automatic",
			"GearboxTest" };
		assertEquals(Set.of(featureNames), featureModel.getFeatures().stream().map(Feature::getName).collect(Collectors
			.toSet()));

		System.out.println(Attributes.TAGS);
	}
}
