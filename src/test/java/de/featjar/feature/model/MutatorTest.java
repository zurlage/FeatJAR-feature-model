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

import static org.junit.jupiter.api.Assertions.assertSame;

import de.featjar.base.data.IMutable;
import de.featjar.base.data.IMutator;
import de.featjar.base.data.identifier.Identifiers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link IMutator} and {@link IMutable}.
 *
 * @author Elias Kuiter
 */
public class MutatorTest {
    FeatureModel featureModel;

    @BeforeEach
    public void createFeatureModel() {
        featureModel = new FeatureModel(Identifiers.newCounterIdentifier());
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
