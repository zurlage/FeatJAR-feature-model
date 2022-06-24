package org.spldev.featuremodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.util.Identifier;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link org.spldev.featuremodel.util.Mutator} and
 * {@link org.spldev.featuremodel.util.Mutable}.
 *
 * @author Elias Kuiter
 */
public class MutatorTest {
	FeatureModel featureModel;

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	public void mutable() {
		assertSame(featureModel.mutate(), featureModel.getMutator());
		assertSame(featureModel, featureModel.mutate().getMutable());
		FeatureModel.Mutator mutator = featureModel.new Mutator();
		featureModel.setMutator(mutator);
		assertSame(mutator, featureModel.getMutator());
	}
}
