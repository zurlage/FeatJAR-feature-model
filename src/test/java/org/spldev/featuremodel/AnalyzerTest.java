package org.spldev.featuremodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spldev.featuremodel.util.Identifier;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link org.spldev.featuremodel.util.Analyzer} and
 * {@link org.spldev.featuremodel.util.Analyzable}.
 *
 * @author Elias Kuiter
 */
public class AnalyzerTest {
	FeatureModel featureModel;

	@BeforeEach
	public void createFeatureModel() {
		featureModel = new FeatureModel(Identifier.newCounter());
	}

	@Test
	public void mutable() {
		assertSame(featureModel.analyze(), featureModel.getAnalyzer());
		assertSame(featureModel, featureModel.analyze().getAnalyzable());
		FeatureModel.Analyzer analyzer = featureModel.new Analyzer();
		featureModel.setAnalyzer(analyzer);
		assertSame(analyzer, featureModel.getAnalyzer());
	}
}
