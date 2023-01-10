/*
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
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.data.Result;
import de.featjar.base.data.identifier.CounterIdentifier;
import de.featjar.base.data.identifier.Identifiers;
import de.featjar.feature.model.mixins.IHasFeatureTree;
import de.featjar.feature.model.order.ComparatorFeatureOrder;
import de.featjar.feature.model.order.ListFeatureOrder;
import de.featjar.formula.structure.Expressions;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FeatureModel}, its elements, and its mixins.
 *
 * @author Elias Kuiter
 */
public class FeatureModelTest {
    FeatureModel featureModel;

    @BeforeEach
    public void createFeatureModel() {
        featureModel = new FeatureModel(Identifiers.newCounterIdentifier());
    }

    @Test
    public void featureModel() {
        Assertions.assertEquals("1", featureModel.getIdentifier().toString());
        assertTrue(featureModel.getFeatureTree().getChildren().isEmpty());
        assertTrue(featureModel.getConstraints().isEmpty());
        assertNotNull(featureModel.getFeatureOrder());
        assertEquals(1, featureModel.featureCache.size());
        assertEquals(1, featureModel.elementCache.size());
    }

    @Test
    public void commonAttributesMixin() {
        Assertions.assertEquals("@1", featureModel.getName());
        Assertions.assertEquals(Result.empty(), featureModel.getDescription());
        featureModel.mutate(m -> {
            m.setName("My Model");
            m.setDescription("awesome description");
        });
        Assertions.assertEquals("My Model", featureModel.getName());
        Assertions.assertEquals(Result.of("awesome description"), featureModel.getDescription());
    }

    @Test
    public void featureModelCacheMixin() {
        featureModel.mutate().createFeatureBelow(featureModel.getRootFeature());
        assertEquals(2, featureModel.featureCache.size());
        featureModel.mutate().createFeatureBelow(featureModel.getRootFeature());
        assertEquals(3, featureModel.featureCache.size());
        IHasFeatureTree.Mutator uncachedMutator = new IHasFeatureTree.Mutator() {
            @Override
            public IFeature newFeature() {
                return new Feature(featureModel);
            }

            @Override
            public IFeatureModel getMutable() {
                return featureModel;
            }
        };
        uncachedMutator.createFeatureBelow(featureModel.getRootFeature());
        assertEquals(3, featureModel.featureCache.size());
        featureModel.mutateInternal(() -> uncachedMutator.createFeatureBelow(featureModel.getRootFeature()));
        assertEquals(5, featureModel.featureCache.size());
    }

    @Test
    public void featureModelConstraintMixin() {
        Assertions.assertEquals(0, featureModel.getNumberOfConstraints());
        IConstraint constraint1 = featureModel.mutate().createConstraint(Expressions.True);
        IConstraint constraint2 = featureModel.mutate().createConstraint(Expressions.True);
        IConstraint constraint3 = featureModel.mutate().createConstraint(Expressions.False);
        Assertions.assertEquals(3, featureModel.getNumberOfConstraints());
        Assertions.assertEquals(Result.of(constraint1), featureModel.getConstraint(constraint1.getIdentifier()));
        Assertions.assertTrue(featureModel.hasConstraint(constraint2.getIdentifier()));
        Assertions.assertEquals(Result.of(0), featureModel.getConstraintIndex(constraint1));
        Assertions.assertEquals(Result.of(1), featureModel.getConstraintIndex(constraint2));
        assertSame(
                constraint3,
                featureModel
                        .getConstraints()
                        .get(featureModel.getConstraintIndex(constraint3).get()));
        constraint2.mutate().remove();
        Assertions.assertEquals(Result.of(0), featureModel.getConstraintIndex(constraint1));
        Assertions.assertEquals(Result.empty(), featureModel.getConstraintIndex(constraint2));
        Assertions.assertEquals(Result.of(1), featureModel.getConstraintIndex(constraint3));
        assertSame(
                constraint3,
                featureModel
                        .getConstraints()
                        .get(featureModel.getConstraintIndex(constraint3).get()));
    }

    @Test
    public void featureModelFeatureOrderMixin() {
        IFeature rootFeature = featureModel.getRootFeature();
        IFeature childFeature = rootFeature.mutate().createFeatureBelow();
        Assertions.assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
        featureModel.mutate().setFeatureOrder(new ListFeatureOrder(Arrays.asList(childFeature, rootFeature)));
        Assertions.assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
        featureModel.mutate().setFeatureOrder(new ListFeatureOrder(Collections.singletonList(childFeature)));
        Assertions.assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
        featureModel.mutate().setFeatureOrder(new ListFeatureOrder(Collections.singletonList(rootFeature)));
        Assertions.assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
        Comparator<IFeature> comparator =
                Comparator.comparingLong(f -> ((CounterIdentifier) f.getIdentifier()).getCounter());
        featureModel.mutate().setFeatureOrder(new ComparatorFeatureOrder(comparator));
        Assertions.assertEquals(Arrays.asList(rootFeature, childFeature), featureModel.getOrderedFeatures());
        featureModel.mutate().setFeatureOrder(new ComparatorFeatureOrder(comparator.reversed()));
        Assertions.assertEquals(Arrays.asList(childFeature, rootFeature), featureModel.getOrderedFeatures());
        IFeature anotherChildFeature = rootFeature.mutate().createFeatureBelow();
        Assertions.assertEquals(
                Arrays.asList(anotherChildFeature, childFeature, rootFeature), featureModel.getOrderedFeatures());
        featureModel.mutate().removeFeature(childFeature);
        featureModel.mutate().removeFeature(anotherChildFeature);
        Assertions.assertEquals(List.of(rootFeature), featureModel.getOrderedFeatures());
    }

    @Test
    public void featureModelFeatureTreeMixin() {
        IFeature rootFeature = featureModel.getRootFeature();
        Assertions.assertEquals(1, featureModel.getNumberOfFeatures());
        final IFeature childFeature = rootFeature.mutate().createFeatureBelow();
        assertSame(childFeature, childFeature.getFeatureTree().getFeature());
        assertSame(rootFeature, childFeature.getFeatureTree().getParent().get().getFeature());
        assertSame(childFeature.getFeatureTree().getParent().get(), rootFeature.getFeatureTree());
        assertSame(featureModel.getFeature(childFeature.getIdentifier()).get(), childFeature);
        Assertions.assertEquals(2, featureModel.getNumberOfFeatures());
        Assertions.assertEquals(Result.of(childFeature), featureModel.getFeature(childFeature.getIdentifier()));
        Assertions.assertTrue(featureModel.hasFeature(childFeature.getIdentifier()));
        Assertions.assertTrue(featureModel.getFeature("root").isEmpty());
        rootFeature.mutate().setName("root");
        Assertions.assertEquals(Result.of(rootFeature), featureModel.getFeature("root"));
        assertEquals(
                List.of(childFeature.getFeatureTree()),
                rootFeature.getFeatureTree().getChildren());
        assertEquals(
                Result.of(rootFeature.getFeatureTree()),
                childFeature.getFeatureTree().getParent());
        assertThrows(IllegalArgumentException.class, () -> rootFeature.mutate().remove());
        assertDoesNotThrow(() -> childFeature.mutate().remove());
        assertEquals(List.of(), rootFeature.getFeatureTree().getChildren());
        assertEquals(Result.empty(), childFeature.getFeatureTree().getParent());
    }
}
