package org.spldev.model.io;

import org.spldev.model.FeatureModel;
import org.spldev.util.io.format.FormatManager;

/**
 * Manages all formats for {@link FeatureModel feature models}.
 *
 * @author Sebastian Krieter
 */
public class FeatureModelFormatManager extends FormatManager<FeatureModel> {
	private static FeatureModelFormatManager INSTANCE = new FeatureModelFormatManager();

	public static FeatureModelFormatManager getInstance() {
		return INSTANCE;
	}

	private FeatureModelFormatManager() {
	}
}
