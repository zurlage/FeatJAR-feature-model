package org.spldev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.model.util.Identifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests for {@link Identifier} and
 * {@link org.spldev.model.util.Identifiable}.
 *
 * @author Elias Kuiter
 */
public class IdentifierTest {
	FeatureModel featureModel;

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	void identifierCounter() {
		Identifier identifier = Identifier.newCounter();
		assertEquals("1", identifier.toString());
		assertEquals("2", identifier.getNewIdentifier().toString());
		assertEquals("3", identifier.getNewIdentifier().toString());
		assertNotEquals(identifier.toString(), identifier.getNewIdentifier().toString());
		assertEquals(identifier, identifier.getFactory().parse(identifier.toString()));
	}

	@Test
	void identifierUUID() {
		Identifier identifier = Identifier.newUUID();
		assertNotEquals(identifier.toString(), identifier.getNewIdentifier().toString());
		assertEquals(identifier, identifier.getFactory().parse(identifier.toString()));
	}

	@Test
	void identifiable() {
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
}
