package org.spldev.featuremodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.mixins.FeatureModelFeatureTreeMixin;
import org.spldev.featuremodel.util.Identifier;
import org.spldev.formula.structure.atomic.literal.Literal;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FeatureModel}, its elements, and its mixins.
 *
 * @author Elias Kuiter
 */
public class FeatureModelTest {
	FeatureModel featureModel;

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	public void featureModel() {
		assertEquals("1", featureModel.getIdentifier().toString());
		assertTrue(featureModel.getFeatureTree().getChildren().isEmpty());
		assertTrue(featureModel.getConstraints().isEmpty());
		assertNotNull(featureModel.getFeatureOrder());
		assertEquals(1, featureModel.getFeatureCache().size());
		assertEquals(1, featureModel.getElementCache().size());
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
	public void featureModelConstraintMixin() {
		assertEquals(0, featureModel.getNumberOfConstraints());
		Constraint constraint1 = featureModel.mutate().createConstraint(Literal.True);
		Constraint constraint2 = featureModel.mutate().createConstraint(Literal.True);
		Constraint constraint3 = featureModel.mutate().createConstraint(Literal.False);
		assertEquals(3, featureModel.getNumberOfConstraints());
		assertEquals(Optional.of(constraint1), featureModel.getConstraint(constraint1.getIdentifier()));
		assertTrue(featureModel.hasConstraint(constraint2.getIdentifier()));
		assertEquals(Optional.of(0), featureModel.getConstraintIndex(constraint1));
		assertEquals(Optional.of(1), featureModel.getConstraintIndex(constraint2));
		assertSame(constraint3, featureModel.getConstraints().get(featureModel.getConstraintIndex(constraint3).get()));
		constraint2.mutate().remove();
		assertEquals(Optional.of(0), featureModel.getConstraintIndex(constraint1));
		assertEquals(Optional.empty(), featureModel.getConstraintIndex(constraint2));
		assertEquals(Optional.of(1), featureModel.getConstraintIndex(constraint3));
		assertSame(constraint3, featureModel.getConstraints().get(featureModel.getConstraintIndex(constraint3).get()));
	}

	@Test
	public void featureModelFeatureOrderMixin() {
		Feature rootFeature = featureModel.getRootFeature();
		Feature childFeature = rootFeature.mutate().createFeatureBelow();
		assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
		featureModel.mutate().setFeatureOrder(FeatureOrder.ofList(Arrays.asList(childFeature, rootFeature)));
		assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
		featureModel.mutate().setFeatureOrder(FeatureOrder.ofList(Collections.singletonList(childFeature)));
		assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
		featureModel.mutate().setFeatureOrder(FeatureOrder.ofList(Collections.singletonList(rootFeature)));
		assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
		Comparator<Feature> comparator = Comparator.comparingLong(f -> ((Identifier.Counter) f.getIdentifier()).getCounter());
		featureModel.mutate().setFeatureOrder(FeatureOrder.ofComparator(comparator));
		assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
		featureModel.mutate().setFeatureOrder(FeatureOrder.ofComparator(comparator.reversed()));
		assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
		Feature anotherChildFeature = rootFeature.mutate().createFeatureBelow();
		assertEquals(Arrays.asList(anotherChildFeature, childFeature, rootFeature), featureModel.getOrderedFeatures());
		featureModel.mutate().removeFeature(childFeature);
		featureModel.mutate().removeFeature(anotherChildFeature);
		assertEquals(List.of(rootFeature), featureModel.getOrderedFeatures());
	}

	@Test
	public void featureModelFeatureTreeMixin() {
		Feature rootFeature = featureModel.getRootFeature();
		assertEquals(1, featureModel.getNumberOfFeatures());
		final Feature childFeature = rootFeature.mutate().createFeatureBelow();
		assertSame(childFeature, childFeature.getFeatureTree().getFeature());
		assertSame(rootFeature, childFeature.getFeatureTree().getParent().get().getFeature());
		assertSame(childFeature.getFeatureTree().getParent().get(), rootFeature.getFeatureTree());
		assertSame(featureModel.getFeature(childFeature.getIdentifier()).get(), childFeature);
		assertEquals(2, featureModel.getNumberOfFeatures());
		assertEquals(Optional.of(childFeature), featureModel.getFeature(childFeature.getIdentifier()));
		assertTrue(featureModel.hasFeature(childFeature.getIdentifier()));
		assertTrue(featureModel.getFeaturesByName("root").isEmpty());
		rootFeature.mutate().setName("root");
		assertEquals(Set.of(rootFeature), featureModel.getFeaturesByName("root"));
		assertEquals(List.of(childFeature.getFeatureTree()), rootFeature.getFeatureTree().getChildren());
		assertEquals(Optional.of(rootFeature.getFeatureTree()), childFeature.getFeatureTree().getParent());
		assertThrows(IllegalArgumentException.class, () -> rootFeature.mutate().remove());
		assertDoesNotThrow(() -> childFeature.mutate().remove());
		assertEquals(List.of(), rootFeature.getFeatureTree().getChildren());
		assertEquals(Optional.empty(), childFeature.getFeatureTree().getParent());
	}
}
