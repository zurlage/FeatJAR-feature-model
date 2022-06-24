package org.spldev.featuremodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Attribute}, {@link Attributes}, and
 * {@link org.spldev.featuremodel.util.Attributable}.
 *
 * @author Elias Kuiter
 */
public class AttributeTest {
	FeatureModel featureModel;
	Attribute<String> attribute = new Attribute<>("any", "test", String.class);

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	public void attribute() {
		assertEquals("any", attribute.getNamespace());
		assertEquals("test", attribute.getName());
		assertEquals(String.class, attribute.getType());
	}

	@Test
	public void attributableGetSet() {
		Map<Attribute<?>, Object> attributeToValueMap = new HashMap<>();
		Attribute.WithDefaultValue<String> attributeWithDefaultValue = new Attribute.WithDefaultValue<>("test",
			String.class, "default");
		assertEquals(Optional.empty(), featureModel.getAttributeValue(attribute));
		assertEquals("default", featureModel.getAttributeValue(attributeWithDefaultValue));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
		featureModel.mutate().setAttributeValue(attribute, "value");
		attributeToValueMap.put(attribute, "value");
		assertEquals(Optional.of("value"), featureModel.getAttributeValue(attribute));
		assertEquals(Optional.of("value"), attribute.apply(attributeToValueMap));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
		featureModel.mutate().removeAttributeValue(attribute);
		attributeToValueMap.clear();
		assertEquals(Optional.empty(), featureModel.getAttributeValue(attribute));
		assertEquals(attributeToValueMap, featureModel.getAttributeToValueMap());
	}

	@Test
	public void attributableToggle() {
		Attribute.WithDefaultValue<Boolean> booleanAttribute = new Attribute.WithDefaultValue<>("test", Boolean.class,
			false);
		assertEquals(false, featureModel.getAttributeValue(booleanAttribute));
		featureModel.mutate().toggleAttributeValue(booleanAttribute);
		assertEquals(true, featureModel.getAttributeValue(booleanAttribute));
	}

	@Test
	public void attributesName() {
		assertEquals(featureModel.getName(), featureModel.getAttributeValue(Attributes.NAME));
		assertEquals("@" + featureModel.getIdentifier(), featureModel.getName());
		assertEquals(Optional.empty(), featureModel.getDescription());
	}

	@Test
	public void attributesDescription() {
		featureModel.mutate().setDescription("desc");
		assertEquals(Optional.of("desc"), featureModel.getDescription());
		featureModel.mutate().setDescription(null);
		assertEquals(Optional.empty(), featureModel.getDescription());
	}

	@Test
	public void attributesHidden() {
		assertFalse(featureModel.getRootFeature().isHidden());
		featureModel.getRootFeature().mutate().setHidden(true);
		assertTrue(featureModel.getRootFeature().isHidden());
		assertFalse(featureModel.getRootFeature().mutate().toggleHidden());
	}
}
