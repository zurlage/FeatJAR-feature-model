package org.spldev.featuremodel;

import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.util.Identifiable;

/**
 * Defines some useful {@link Attribute attributes} for
 * {@link org.spldev.featuremodel.FeatureModel feature models}, *
 * {@link org.spldev.featuremodel.Feature features}, and
 * {@link org.spldev.featuremodel.Constraint constraints}.
 *
 * @author Elias Kuiter
 */
public class Attributes {
	public static final Attribute.WithDefaultValue<String> NAME = new Attribute.WithDefaultValue<String>(
		"name", String.class, identifiable -> "@" + ((Identifiable) identifiable).getIdentifier().toString());
	public static final Attribute<String> DESCRIPTION = new Attribute<>("description", String.class);
	public static final Attribute.WithDefaultValue<Boolean> HIDDEN = new Attribute.WithDefaultValue<>("hidden",
		Boolean.class, false);
	public static final Attribute.WithDefaultValue<Boolean> ABSTRACT = new Attribute.WithDefaultValue<>("abstract",
		Boolean.class, false);
}
