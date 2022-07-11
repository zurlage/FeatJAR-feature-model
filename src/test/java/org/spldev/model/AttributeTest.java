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
package org.spldev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.model.util.Attribute;
import org.spldev.model.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Attribute}, {@link Attributes}, and
 * {@link org.spldev.model.util.Attributable}.
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
		Attribute.WithDefaultValue<String> attributeWithDefaultValue = new Attribute.WithDefaultValue<>(
			Attribute.DEFAULT_NAMESPACE, "test", String.class, "default");
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
		Attribute.WithDefaultValue<Boolean> booleanAttribute = new Attribute.WithDefaultValue<>(
			Attribute.DEFAULT_NAMESPACE, "test", Boolean.class, false);
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
