package org.spldev.model.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spldev.model.FeatureModel;
import org.spldev.model.io.xml.GraphVizFeatureModelFormat;
import org.spldev.model.io.xml.XMLFeatureModelFormat;
import org.spldev.util.data.Result;
import org.spldev.util.io.IO;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphVizFeatureModelFormatTest {
	@Test
	public void graphVizFeatureModelFormat() throws IOException {
		Result<FeatureModel> featureModel = IO.load(Paths.get(
			"src/test/resources/testFeatureModels/car.xml"), new XMLFeatureModelFormat());
		assertTrue(featureModel.isPresent());
		assertTrue(IO.print(featureModel.get(), new GraphVizFeatureModelFormat()).startsWith(
			"digraph {"));
	}
}
