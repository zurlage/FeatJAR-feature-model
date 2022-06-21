package org.spldev.featuremodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.io.FeatureModelFormatManager;
import org.spldev.featuremodel.io.xml.XmlFeatureModelFormat;
import org.spldev.featuremodel.mixins.FeatureModelFeatureTreeMixin;
import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.formula.io.FormulaFormatManager;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.Literal;
import org.spldev.util.data.Result;
import org.spldev.util.io.FileHandler;
import org.spldev.util.tree.Trees;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureModelTest {
	FeatureModel featureModel;

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	void identifier() {
		Identifier identifier = Identifier.newCounter();
		featureModel = new FeatureModel(identifier);
		assertEquals("1", featureModel.getIdentifier().toString());
		assertEquals("2", featureModel.getRootFeature().getIdentifier().toString());
		assertEquals("3", identifier.getFactory().get().toString());
		assertEquals("4", featureModel.getRootFeature().getNewIdentifier().toString());
		featureModel = new FeatureModel(identifier.getNewIdentifier());
		assertEquals("5", featureModel.getIdentifier().toString());
		assertEquals("6", featureModel.getRootFeature().getIdentifier().toString());
		assertEquals("7", featureModel.getNewIdentifier().toString());
		assertEquals("3", new FeatureModel(Identifier.newCounter()).getNewIdentifier().toString());
	}

	@Test
	public void attribute() {
		Attribute<String> attribute = new Attribute<>("test");
		Map<Attribute<?>, Object> attributeToValueMap = new HashMap<>();
		Attribute.WithDefaultValue<String> attributeWithDefaultValue = new Attribute.WithDefaultValue<>("test", "default");
		assertEquals(Optional.empty(), featureModel.getAttributeValue(attribute));
		assertEquals("default", featureModel.getAttributeValue(attributeWithDefaultValue));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
		featureModel.mutate(m -> m.setAttributeValue(attribute, "value"));
		attributeToValueMap.put(attribute, "value");
		assertEquals(Optional.of("value"), featureModel.getAttributeValue(attribute));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
		featureModel.mutate(m -> m.removeAttributeValue(attribute));
		attributeToValueMap.clear();
		assertEquals(Optional.empty(), featureModel.getAttributeValue(attribute));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
	}

	@Test
	public void mutable() {
		assertSame(featureModel, featureModel.mutate().getMutable());
		featureModel.mutate(mutator -> assertSame(mutator, featureModel.getMutator()));
		featureModel.mutate(mutator -> assertSame(featureModel, mutator.getMutable()));
		FeatureModel.Mutator mutator = featureModel.new Mutator();
		featureModel.setMutator(mutator);
		assertSame(mutator, featureModel.getMutator());
		// todo: mutate unsafely
	}

	@Test
	public void commonAttributesMixin() {
		assertEquals("@1", featureModel.getName());
		assertEquals(Optional.empty(), featureModel.getDescription());
		featureModel.mutate(m -> {
			m.setName("My Model");
			m.setDescription("awesome description");
		});
		assertEquals("My Model", featureModel.getName());
		assertEquals(Optional.of("awesome description"), featureModel.getDescription());
	}

	@Test
	public void featureModelCacheMixin() {
		featureModel.mutate().createFeatureBelow(featureModel.getRootFeature());
		assertEquals(2, featureModel.getFeatureCache().size());
		featureModel.mutate().createFeatureBelow(featureModel.getRootFeature());
		assertEquals(3, featureModel.getFeatureCache().size());
		FeatureModelFeatureTreeMixin.Mutator uncachedMutator = () -> featureModel;
		uncachedMutator.createFeatureBelow(featureModel.getRootFeature());
		assertEquals(3, featureModel.getFeatureCache().size());
		featureModel.mutateInternal(() -> uncachedMutator.createFeatureBelow(featureModel.getRootFeature()));
		assertEquals(5, featureModel.getFeatureCache().size());
	}

	@Test
	public void featureTree() {
		final FeatureModel featureModel = new FeatureModel(Identifier.newCounter());
		final Feature feature = featureModel.mutate().createFeatureBelow(featureModel.getRootFeature());
		assertSame(feature, feature.getFeatureTree().getFeature());
		assertSame(featureModel.getRootFeature(), feature.getFeatureTree().getParent().get().getFeature());
		assertSame(feature.getFeatureTree().getParent().get(), featureModel.getRootFeature().getFeatureTree());
		assertSame(featureModel.getFeature(feature.getIdentifier()).get(), feature);
	}

	@Test
	public void xmlFeatureModelFormat() {
		Result<FeatureModel> featureModelResult = FileHandler.load(Paths.get("src/test/resources/testFeatureModels/car.xml"), new XmlFeatureModelFormat());
		assertTrue(featureModelResult.isPresent());
		FeatureModel featureModel = featureModelResult.get();
		String[] featureNames = new String[] {"Car", "Carbody", "Radio", "Ports", "USB", "CD", "Navigation", "DigitalCards", "Europe", "USA", "GPSAntenna", "Bluetooth", "Gearbox", "Manual", "Automatic", "GearboxTest"};
		assertEquals(Set.of(featureNames), featureModel.getFeatures().stream().map(Feature::getName).collect(Collectors.toSet()));
		System.out.println(featureModel);
	}
}
