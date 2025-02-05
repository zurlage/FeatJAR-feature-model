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

import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.data.identifier.Identifiers;
import de.featjar.feature.model.FeatureModel;
import de.featjar.feature.model.IFeature;
import de.featjar.feature.model.IFeatureTree;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.*;
import de.featjar.formula.structure.predicate.Literal;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link de.featjar.feature.model.configuration} configuration.
 * @author Luca zur Lage
 * @author Werner Münch
 * @author Tom Röhrig
 */
public class ConfigurationTest {

    private static FeatureModel featureModel;
    private static Configuration configuration;

    @BeforeAll
    public static void setupTestConfiguration() {

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

        ConfigurationTest.featureModel = featureModel;
        ConfigurationTest.configuration = new Configuration(featureModel);
    }

    @Test
    public void testfeatureModelToConfigurationAsSet() {
        Set<IFeature> configurationFeatures = configuration.getFeatures().stream()
                .map(SelectableFeature::getFeature)
                .collect(Collectors.toSet());

        Set<IFeature> featureModelFeatures = new LinkedHashSet<>(featureModel.getFeatures());

        assertEquals(featureModelFeatures, configurationFeatures);
    }

    @Test
    public void testConfigurationCloning() {

        // configuration setup
        FeatureModel featureModelForCloning = new FeatureModel(Identifiers.newCounterIdentifier());

        // features
        IFeatureTree rootTree = featureModelForCloning
                .mutate()
                .addFeatureTreeRoot(featureModelForCloning.mutate().addFeature("root"));
        rootTree.mutate().setAnd();

        IFeature childFeature1 = featureModelForCloning.mutate().addFeature("Test1");
        IFeatureTree childTree1 = rootTree.mutate().addFeatureBelow(childFeature1);

        IFeature childFeature2 = featureModelForCloning.mutate().addFeature("Test2");
        IFeatureTree childTree2 = rootTree.mutate().addFeatureBelow(childFeature2);

        IFeature childFeature3 = featureModelForCloning.mutate().addFeature("Test3");
        IFeatureTree childTree3 = childTree1.mutate().addFeatureBelow(childFeature3);
        childTree3.mutate().setAlternative();

        IFeature childFeature4 = featureModelForCloning.mutate().addFeature("Test4");
        childTree1.mutate().addFeatureBelow(childFeature4);

        IFeature childFeature5 = featureModelForCloning.mutate().addFeature("Test5");
        IFeatureTree childTree5 = childTree2.mutate().addFeatureBelow(childFeature5);
        childTree5.mutate().setOr();

        IFeature childFeature6 = featureModelForCloning.mutate().addFeature("Test6");
        childTree2.mutate().addFeatureBelow(childFeature6);

        IFeature childFeature7 = featureModelForCloning.mutate().addFeature("Test7");
        IFeatureTree childTree7 = rootTree.mutate().addFeatureBelow(childFeature7);
        childTree7.mutate().setMandatory();

        IFormula formula1 = new Or(
                new And(new Literal("Test1"), new Literal("Test2")),
                new BiImplies(new Literal("Test3"), new Literal("Test4")),
                new Implies(new Literal("Test5"), new Literal("Test6")),
                new Not(new Literal("Test7")));

        // constraints
        featureModelForCloning.mutate().addConstraint(formula1);

        Configuration configurationForCloning = new Configuration(featureModelForCloning);

        // selection setup
        configurationForCloning.setManual("root", Selection.SELECTED);
        configurationForCloning.setAutomatic("Test1", Selection.SELECTED);
        configurationForCloning.setManual("Test2", Selection.SELECTED);
        configurationForCloning.setAutomatic("Test3", Selection.UNSELECTED);
        configurationForCloning.setManual("Test4", Selection.UNSELECTED);

        // execute clone()
        Configuration clonedConfiguration = configurationForCloning.clone();

        // check for same feature model
        assertEquals(configurationForCloning.getFeatureModel(), clonedConfiguration.getFeatureModel());

        // check all selectable features
        for (SelectableFeature originalFeature :
                configurationForCloning.getSelectableFeatures().values()) {
            assertNotNull(clonedConfiguration.getSelectableFeature(originalFeature.getName()));
        }

        // check whether the selections of the clonedConfiguration are the same as for configurationForCloning
        assertEquals(
                Selection.SELECTED,
                clonedConfiguration.getSelectableFeature("root").getManual());
        assertEquals(
                Selection.SELECTED,
                clonedConfiguration.getSelectableFeature("Test1").getAutomatic());
        assertEquals(
                Selection.SELECTED,
                clonedConfiguration.getSelectableFeature("Test2").getManual());

        assertEquals(
                Selection.UNSELECTED,
                clonedConfiguration.getSelectableFeature("Test3").getAutomatic());
        assertEquals(
                Selection.UNSELECTED,
                clonedConfiguration.getSelectableFeature("Test4").getManual());

        assertEquals(
                Selection.UNDEFINED,
                clonedConfiguration.getSelectableFeature("Test5").getSelection());
        assertEquals(
                Selection.UNDEFINED,
                clonedConfiguration.getSelectableFeature("Test6").getSelection());
        assertEquals(
                Selection.UNDEFINED,
                clonedConfiguration.getSelectableFeature("Test7").getSelection());
    }

    @Test
    public void testSelectionAttributesSetterAndGetterOfAutomaticAndManual() {
        SelectableFeature testFeature = new SelectableFeature("testFeature");

        // Initial state
        assertEquals(Selection.UNDEFINED, testFeature.getAutomatic());
        assertEquals(Selection.UNDEFINED, testFeature.getManual());

        // Test manual selection to SELECTED
        testFeature.setManual(Selection.SELECTED);
        assertEquals(Selection.UNDEFINED, testFeature.getAutomatic());
        assertEquals(Selection.SELECTED, testFeature.getManual());
        assertThrows(AutomaticalSelectionNotPossibleException.class, () -> {
            testFeature.setAutomatic(Selection.UNSELECTED);
        });
        testFeature.setManual(Selection.UNDEFINED);

        // Test automatic selection to SELECTED
        testFeature.setAutomatic(Selection.SELECTED);
        assertEquals(Selection.SELECTED, testFeature.getAutomatic());
        assertEquals(Selection.UNDEFINED, testFeature.getManual());
        assertThrows(SelectionNotPossibleException.class, () -> {
            testFeature.setManual(Selection.UNSELECTED);
        });
        testFeature.setAutomatic(Selection.UNDEFINED);

        // Test manual selection to UNSELECTED
        testFeature.setManual(Selection.UNSELECTED);
        assertEquals(Selection.UNDEFINED, testFeature.getAutomatic());
        assertEquals(Selection.UNSELECTED, testFeature.getManual());
        assertThrows(AutomaticalSelectionNotPossibleException.class, () -> {
            testFeature.setAutomatic(Selection.SELECTED);
        });
        testFeature.setManual(Selection.UNDEFINED);

        // Test automatic selection to UNSELECTED
        testFeature.setAutomatic(Selection.UNSELECTED);
        assertEquals(Selection.UNSELECTED, testFeature.getAutomatic());
        assertEquals(Selection.UNDEFINED, testFeature.getManual());
        assertThrows(SelectionNotPossibleException.class, () -> {
            testFeature.setManual(Selection.SELECTED);
        });
        testFeature.setAutomatic(Selection.UNDEFINED);

        // Test setting both manual and automatic to SELECTED
        testFeature.setManual(Selection.SELECTED);
        testFeature.setAutomatic(Selection.SELECTED);
        assertEquals(Selection.SELECTED, testFeature.getManual());
        assertEquals(Selection.SELECTED, testFeature.getAutomatic());
        testFeature.setManual(Selection.UNDEFINED);
        testFeature.setAutomatic(Selection.UNDEFINED);

        // Test setting both manual and automatic to UNSELECTED
        testFeature.setManual(Selection.UNSELECTED);
        testFeature.setAutomatic(Selection.UNSELECTED);
        assertEquals(Selection.UNSELECTED, testFeature.getManual());
        assertEquals(Selection.UNSELECTED, testFeature.getAutomatic());
        testFeature.setManual(Selection.UNDEFINED);
        testFeature.setAutomatic(Selection.UNDEFINED);
    }

    @Test
    public void testSelectionAttributesSetterAndGetterOfAutomaticAndManualFromATestConfiguration() {

        Configuration testConfiguration = configuration.clone();

        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getSelectableFeature("Test1").getAutomatic());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getSelectableFeature("Test2").getManual());

        testConfiguration.setAutomatic("Test1", Selection.SELECTED);
        testConfiguration.setManual("Test2", Selection.SELECTED);

        assertEquals(
                Selection.SELECTED,
                testConfiguration.getSelectableFeature("Test1").getAutomatic());
        assertEquals(
                Selection.SELECTED,
                testConfiguration.getSelectableFeature("Test2").getManual());
        assertEquals(
                Selection.SELECTED,
                testConfiguration.getSelectableFeature("Test1").getSelection());
        assertEquals(
                Selection.SELECTED,
                testConfiguration.getSelectableFeature("Test2").getSelection());

        testConfiguration.setAutomatic("Test1", Selection.UNSELECTED);
        testConfiguration.setManual("Test2", Selection.UNSELECTED);

        assertEquals(
                Selection.UNSELECTED,
                testConfiguration.getSelectableFeature("Test1").getAutomatic());
        assertEquals(
                Selection.UNSELECTED,
                testConfiguration.getSelectableFeature("Test2").getManual());
        assertEquals(
                Selection.UNSELECTED,
                testConfiguration.getSelectableFeature("Test1").getSelection());
        assertEquals(
                Selection.UNSELECTED,
                testConfiguration.getSelectableFeature("Test2").getSelection());
    }

    @Test
    public void testResetValuesShouldClearSelectionAttributes() {

        Configuration testConfiguration = configuration.clone();

        // selection setup
        testConfiguration.setManual("root", Selection.SELECTED);
        testConfiguration.setAutomatic("Test1", Selection.SELECTED);
        testConfiguration.setManual("Test2", Selection.SELECTED);
        testConfiguration.setAutomatic("Test3", Selection.UNSELECTED);
        testConfiguration.setManual("Test4", Selection.UNSELECTED);

        // check selection before resetValues()
        LinkedHashMap<String, SelectableFeature> selectableFeaturesBefore = testConfiguration.getSelectableFeatures();

        // check all SELECTED features in LinkedHashMap selectableFeatures
        assertEquals("root", selectableFeaturesBefore.get("root").getName());
        assertEquals(Selection.SELECTED, selectableFeaturesBefore.get("root").getSelection());
        assertEquals("Test1", selectableFeaturesBefore.get("Test1").getName());
        assertEquals(Selection.SELECTED, selectableFeaturesBefore.get("Test1").getSelection());
        assertEquals("Test2", selectableFeaturesBefore.get("Test2").getName());
        assertEquals(Selection.SELECTED, selectableFeaturesBefore.get("Test2").getSelection());

        // check all UNSELECTED features in LinkedHashMap selectableFeatures
        assertEquals("Test3", selectableFeaturesBefore.get("Test3").getName());
        assertEquals(Selection.UNSELECTED, selectableFeaturesBefore.get("Test3").getSelection());
        assertEquals("Test4", selectableFeaturesBefore.get("Test4").getName());
        assertEquals(Selection.UNSELECTED, selectableFeaturesBefore.get("Test4").getSelection());

        // check all UNDEFINED features in LinkedHashMap selectableFeatures
        assertEquals("Test5", selectableFeaturesBefore.get("Test5").getName());
        assertEquals(Selection.UNDEFINED, selectableFeaturesBefore.get("Test5").getSelection());
        assertEquals("Test6", selectableFeaturesBefore.get("Test6").getName());
        assertEquals(Selection.UNDEFINED, selectableFeaturesBefore.get("Test6").getSelection());
        assertEquals("Test7", selectableFeaturesBefore.get("Test7").getName());
        assertEquals(Selection.UNDEFINED, selectableFeaturesBefore.get("Test7").getSelection());

        // execute resetValues
        testConfiguration.resetValues();

        // selected, unselected and automatic features should be empty after resetValues()
        assertTrue(testConfiguration.getSelectedFeatures().isEmpty());
        assertTrue(testConfiguration.getUnSelectedFeatures().isEmpty());
        assertTrue(testConfiguration.getAutomaticFeatures().isEmpty());

        // manual should contain all selectables with selection UNDEFINED after resetValues()
        assertEquals("root", testConfiguration.getManualFeatures().get(0).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(0).getSelection());
        assertEquals("Test1", testConfiguration.getManualFeatures().get(1).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(1).getSelection());
        assertEquals("Test2", testConfiguration.getManualFeatures().get(2).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(2).getSelection());
        assertEquals("Test3", testConfiguration.getManualFeatures().get(3).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(3).getSelection());
        assertEquals("Test4", testConfiguration.getManualFeatures().get(4).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(4).getSelection());
        assertEquals("Test5", testConfiguration.getManualFeatures().get(5).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(5).getSelection());
        assertEquals("Test6", testConfiguration.getManualFeatures().get(6).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(6).getSelection());
        assertEquals("Test7", testConfiguration.getManualFeatures().get(7).getName());
        assertEquals(
                Selection.UNDEFINED,
                testConfiguration.getManualFeatures().get(7).getSelection());

        // selectable features after execution of resetValues()
        LinkedHashMap<String, SelectableFeature> selectableFeaturesAfter = testConfiguration.getSelectableFeatures();

        // selectable features should remain unchanged
        assertEquals(selectableFeaturesBefore, selectableFeaturesAfter);
    }

    @Test
    public void testResetAutomaticValuesShouldOnlyClearAutomaticAttributes() {

        Configuration testConfiguration = configuration.clone();

        // create list of automatic features
        List<SelectableFeature> automaticFeatures = new ArrayList<>();
        SelectableFeature af1 = new SelectableFeature("Test1");
        af1.setAutomatic(Selection.SELECTED);
        SelectableFeature af2 = new SelectableFeature("Test3");
        af2.setAutomatic(Selection.SELECTED);

        automaticFeatures.add(af1);
        automaticFeatures.add(af2);

        // create list of manual features
        List<SelectableFeature> manualFeatures = new ArrayList<>();
        SelectableFeature mf1 = new SelectableFeature("Test2");
        mf1.setManual(Selection.SELECTED);
        SelectableFeature mf2 = new SelectableFeature("Test4");
        mf2.setManual(Selection.SELECTED);

        manualFeatures.add(mf1);
        manualFeatures.add(mf2);

        // setup for testConfiguration
        testConfiguration.setAutomatic("Test1", Selection.SELECTED);
        testConfiguration.setAutomatic("Test3", Selection.SELECTED);
        testConfiguration.setManual("Test2", Selection.SELECTED);
        testConfiguration.setManual("Test4", Selection.SELECTED);

        // comparison of created automaticFeatures and manualFeatures,
        // with setup of the test configuration
        boolean selectableFeatureFound = false;
        for (SelectableFeature sf : testConfiguration.getSelectableFeatures().values()) {
            if (testConfiguration.getAutomaticFeatures().contains(sf)) {
                for (SelectableFeature af : automaticFeatures) {
                    if (af.getName().equals(sf.getName())) {
                        selectableFeatureFound = true;
                        assertEquals(
                                Selection.SELECTED,
                                af.getAutomatic(),
                                "Automatic feature " + af + " is not selected, but should be selected!");
                    }
                }

            } else if (testConfiguration.getManualFeatures().contains(sf)) {
                for (SelectableFeature mf : manualFeatures) {
                    if (mf.getName().equals(sf.getName())) {
                        selectableFeatureFound = true;
                        assertEquals(
                                Selection.SELECTED,
                                mf.getManual(),
                                "Automatic feature " + sf + " is not selected, but should be selected!");
                        break;
                    }
                }
                // manual features which are undefined or unselected
                if (!selectableFeatureFound) {
                    if (sf.getSelection() == Selection.UNDEFINED || sf.getSelection() == Selection.UNSELECTED) {
                        selectableFeatureFound = true;
                    }
                }
            }
            assertTrue(selectableFeatureFound);
            selectableFeatureFound = false;
        }

        // execute resetAutomaticValues()
        testConfiguration.resetAutomaticValues();

        // there should be no automatic features anymore
        assertTrue(testConfiguration.getAutomaticFeatures().isEmpty());

        // check whether the manual features are unchanged
        selectableFeatureFound = false;
        for (SelectableFeature sf : testConfiguration.getSelectableFeatures().values()) {
            if (testConfiguration.getManualFeatures().contains(sf)) {
                for (SelectableFeature mf : manualFeatures) {
                    if (mf.getName().equals(sf.getName())) {
                        selectableFeatureFound = true;
                        assertEquals(Selection.SELECTED, mf.getManual());
                        break;
                    }
                }
                // manual features which are undefined or unselected
                if (!selectableFeatureFound) {
                    if (sf.getSelection() == Selection.UNDEFINED || sf.getSelection() == Selection.UNSELECTED) {
                        selectableFeatureFound = true;
                    }
                }
            }
            assertTrue(selectableFeatureFound);
            selectableFeatureFound = false;
        }
    }
}
