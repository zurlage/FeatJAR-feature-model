package org.spldev.featuremodel.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.io.xml.GraphVizFeatureModelFormat;
import org.spldev.featuremodel.io.xml.XMLFeatureModelFormat;
import org.spldev.util.io.FileHandler;

import java.io.IOException;
import java.nio.file.Paths;

public class GraphVizFeatureModelFormatTest {
	@Test
	public void xmlFeatureModelFormat() throws IOException {
		FeatureModel featureModel = FileHandler.load(Paths.get(
			"src/test/resources/testFeatureModels/car.xml"), new XMLFeatureModelFormat()).get();
		Assertions.assertTrue(FileHandler.print(featureModel, new GraphVizFeatureModelFormat()).startsWith("digraph {"));
	}
}
