/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.spldev.featuremodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.LiteralPredicate;
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.formula.structure.compound.Implies;
import org.spldev.formula.structure.term.bool.BoolVariable;

/**
 * Tests for the {@link FeatureModel}.
 *
 * @author Jens Meinicke
 * @author Marlen Bernier
 * @author Dawid Szczepanski
 */
public class FeatureModelTest {

	private static final FeatureModelFactory factory = FeatureModelFactory.getInstance();

	@Test
	public void recordGetFeatureName() {
		final FeatureModel fm = factory.get();
		final Feature feature = factory.createFeature(fm, "test_root");
		fm.addFeature(feature);
		fm.getStructure().setRoot(feature.getStructure());
		final Feature root = fm.getFeature("test_root");
		assertSame(root.getStructure(), fm.getStructure().getRoot());

		final FeatureModel clonedModel = fm.clone(null);
		final Feature root2 = clonedModel.getFeature("test_root");

		assertSame(root2.getStructure(), clonedModel.getStructure().getRoot());
	}

	@Test
	public void getFeatureOrderListTest() {
		final FeatureModel fm = factory.get();
		final Collection<String> expectedOrder = new LinkedList<>();
		Collection<String> actualOrder = fm.getFeatureOrderList();
		assertEquals(expectedOrder, actualOrder);

		final Feature root = factory.createFeature(fm, "root");
		fm.addFeature(root);
		fm.getStructure().setRoot(root.getStructure());
		expectedOrder.add(root.getName());
		actualOrder = fm.getFeatureOrderList();
		assertEquals(expectedOrder, actualOrder);

		final Feature A = factory.createFeature(fm, "A");
		FeatureModels.addChild(root, A);
		expectedOrder.add(A.getName());
		actualOrder = fm.getFeatureOrderList();
		assertEquals(expectedOrder, actualOrder);

		final Feature B = factory.createFeature(fm, "B");
		FeatureModels.addChild(root, B);
		expectedOrder.add(B.getName());
		actualOrder = fm.getFeatureOrderList();
		assertEquals(expectedOrder, actualOrder);

		final Feature C = factory.createFeature(fm, "C");
		FeatureModels.addChild(B, C);
		expectedOrder.add(C.getName());
		actualOrder = fm.getFeatureOrderList();
		assertEquals(expectedOrder, actualOrder);
	}

	/**
	 * After adding new fields to the IConstraint implementation, you should test it
	 * in a test similar to this.
	 */
	@Test
	public void cloneFeatureModelTestDescription() {
		final FeatureModel fm = factory.get();
		final Feature feature = factory.createFeature(fm, "test_root_original");
		final Feature feature2 = factory.createFeature(fm, "test_root_original2");
		fm.addFeature(feature);
		fm.addFeature(feature2);
		fm.getStructure().setRoot(feature.getStructure());
		final Feature root = fm.getFeature("test_root_original");
		assertSame(root.getStructure(), fm.getStructure().getRoot());

		final VariableMap map = VariableMap.fromNames(Arrays.asList("test_root_original", "test_root_original2"));
		LiteralPredicate expression1 = new LiteralPredicate((BoolVariable) map.getVariable("test_root_original").get(),
			true);
		LiteralPredicate expression2 = new LiteralPredicate((BoolVariable) map.getVariable("test_root_original2").get(),
			true);
		final Formula constraintNode = new Implies(expression1, expression2);
		final Constraint constraint = factory.createConstraint(fm, constraintNode);
		final String originalDescription = "Constraint Description Test";
		constraint.setDescription(originalDescription);
		fm.addConstraint(constraint);

		final FeatureModel clonedModel = fm.clone(null);

		for (final Constraint constraintClone : clonedModel.getConstraints()) {
			final String descriptionCopy = constraintClone.getDescription();
			assertEquals(originalDescription, descriptionCopy);
		}
	}

}
