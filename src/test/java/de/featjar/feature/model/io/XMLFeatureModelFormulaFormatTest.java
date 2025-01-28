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
package de.featjar.feature.model.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.featjar.Common;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.data.Sets;
import de.featjar.base.data.identifier.Identifiers;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.input.FileInputMapper;
import de.featjar.base.io.input.StringInputMapper;
import de.featjar.feature.model.FeatureModel;
import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.IFeatureModel;
import de.featjar.feature.model.IFeatureTree;
import de.featjar.feature.model.io.xml.XMLFeatureModelFormat;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.*;
import de.featjar.formula.structure.predicate.Literal;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class XMLFeatureModelFormulaFormatTest extends Common {

    private static FeatureModel featureModel;

    @BeforeAll
    public static void setup() {
        FeatureModel featureModel = new FeatureModel(Identifiers.newCounterIdentifier());

        // features
        IFeatureTree rootTree =
                featureModel.mutate().addFeatureTreeRoot(featureModel.mutate().addFeature("root"));
        rootTree.mutate().setAnd();

        IFeature childFeature1 = featureModel.mutate().addFeature("Test1");
        IFeatureTree childTree1 = rootTree.mutate().addFeatureBelow(childFeature1);

        IFeature childFeature2 = featureModel.mutate().addFeature("Test2");
        IFeatureTree childTree2 = rootTree.mutate().addFeatureBelow(childFeature2);

        IFeature childFeature3 = featureModel.mutate().addFeature("Test3");
        IFeatureTree childTree3 = childTree1.mutate().addFeatureBelow(childFeature3);
        childTree3.mutate().setAlternative();

        IFeature childFeature4 = featureModel.mutate().addFeature("Test4");
        childTree1.mutate().addFeatureBelow(childFeature4);

        IFeature childFeature5 = featureModel.mutate().addFeature("Test5");
        IFeatureTree childTree5 = childTree2.mutate().addFeatureBelow(childFeature5);
        childTree5.mutate().setOr();

        IFeature childFeature6 = featureModel.mutate().addFeature("Test6");
        childTree2.mutate().addFeatureBelow(childFeature6);

        IFeature childFeature7 = featureModel.mutate().addFeature("Test7");
        IFeatureTree childTree7 = rootTree.mutate().addFeatureBelow(childFeature7);
        childTree7.mutate().setMandatory();

        IFormula formula1 = new Or(
                new And(new Literal("Test1"), new Literal("Test2")),
                new BiImplies(new Literal("Test3"), new Literal("Test4")),
                new Implies(new Literal("Test5"), new Literal("Test6")),
                new Not(new Literal("Test7")));

        // constraints
        featureModel.mutate().addConstraint(formula1);

        XMLFeatureModelFormulaFormatTest.featureModel = featureModel;
    }

    @Test
    public void xmlFeatureModelFormat() {
        IFeatureModel featureModelResult = load("testFeatureModels/car.xml", new XMLFeatureModelFormat());
        IFeatureModel featureModel = featureModelResult;
        String[] featureNames = new String[] {
                "Car",
                "Carbody",
                "Radio",
                "Ports",
                "USB",
                "CD",
                "Navigation",
                "DigitalCards",
                "Europe",
                "USA",
                "GPSAntenna",
                "Bluetooth",
                "Gearbox",
                "Manual",
                "Automatic",
                "GearboxTest"
        };
        assertEquals(
                Sets.of(featureNames),
                featureModel.getFeatures().stream()
                        .map(IFeature::getName)
                        .map(Result::get)
                        .collect(Sets.toSet()));
    }

    @Test
    void testXMLFileToFeatureModelToXMLFile() throws IOException {
        Path xmlFile = Path.of("src", "test", "resources", "xml", "car.xml");
        String fileContent = new String(Files.readAllBytes(xmlFile), StandardCharsets.UTF_8);

        IFormat<IFeatureModel> format = new XMLFeatureModelFormat();
        Result<IFeatureModel> parseResult = format.parse(new FileInputMapper(xmlFile, StandardCharsets.UTF_8));

        Assertions.assertTrue(parseResult.isPresent(), Problem.printProblems(parseResult.getProblems()));
        IFeatureModel parsedFeatureModel = parseResult.get();

        Result<String> serializedResult = format.serialize(parsedFeatureModel);

        Assertions.assertTrue(serializedResult.isPresent(), "Serialization of IFeatureModel failed");
        String serializedContent = serializedResult.get();

        Assertions.assertTrue(
                Objects.equals(fileContent.replaceAll("\r", ""), serializedContent.replaceAll("\r", "")),
                "Serialized content does not match the original file content");
    }

    // TODO: Need to assert objects.equals for each featuremodel.element instead of for the featuremodel itself.

    // @Test
    void testFeatureModelToXMLStringToFeatureModel() throws IOException {
        IFormat<IFeatureModel> format = new XMLFeatureModelFormat();

        Result<String> serializedResult = format.serialize(featureModel);
        Assertions.assertTrue(serializedResult.isPresent(), "Serialization of IFeatureModel failed");

        String serializedFeatureModel = serializedResult.get();
        Result<IFeatureModel> parseResult =
                format.parse(new StringInputMapper(serializedFeatureModel, StandardCharsets.UTF_8, "xml"));

        Assertions.assertTrue(parseResult.isPresent(), Problem.printProblems(parseResult.getProblems()));
        IFeatureModel parsedFeatureModel = parseResult.get();

        Assertions.assertTrue(Objects.equals(parsedFeatureModel, featureModel));
    }
}
