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

import de.featjar.base.data.IAnalyzable;
import de.featjar.base.data.IAnalyzer;
import de.featjar.base.data.AIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link IAnalyzer} and {@link IAnalyzable}.
 *
 * @author Elias Kuiter
 */
public class AnalyzerTest {
    FeatureModel featureModel;

    @BeforeEach
    public void createFeatureModel() {
        featureModel = new FeatureModel(AIdentifier.newCounter());
    }

    @Test
    public void analyzable() {
        assertSame(featureModel.analyze(), featureModel.getAnalyzer());
        assertSame(featureModel, featureModel.analyze().getAnalyzable());
        FeatureModel.Analyzer analyzer = featureModel.new Analyzer();
        featureModel.setAnalyzer(analyzer);
        assertSame(analyzer, featureModel.getAnalyzer());
    }
}
