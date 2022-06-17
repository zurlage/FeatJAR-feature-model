package org.spldev.featuremodel;

import java.util.*;

/**
 * Feature model element
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class Element extends AttributeContainer {
	protected final Identifier<?> identifier;

	protected final PureFeatureModel featureModel;
	public Element(Identifier<?> identifier, PureFeatureModel featureModel) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(featureModel);
		this.identifier = identifier;
		this.featureModel = featureModel;
	}

	public Identifier<?> getIdentifier() {
		return identifier;
	}

	public PureFeatureModel getFeatureModel() {
		return featureModel;
	}
}
