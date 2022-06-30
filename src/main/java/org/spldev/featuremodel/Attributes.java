package org.spldev.featuremodel;

import org.spldev.featuremodel.util.Attributable;
import org.spldev.featuremodel.util.Attribute;
import org.spldev.featuremodel.util.Identifiable;
import org.spldev.util.data.Problem;

import java.util.*;
import java.util.function.Function;

/**
 * Defines some useful {@link Attribute attributes} for
 * {@link org.spldev.featuremodel.FeatureModel feature models},
 * {@link org.spldev.featuremodel.Feature features}, and
 * {@link org.spldev.featuremodel.Constraint constraints}.
 *
 * @author Elias Kuiter
 */
public class Attributes {
	public static final String NAMESPACE = Attributes.class.getCanonicalName();
	public static final Attribute.WithDefaultValue<String> NAME = new Attribute.WithDefaultValue<String>(NAMESPACE,
		"name", String.class, identifiable -> "@" + ((Identifiable) identifiable).getIdentifier().toString());
	public static final Attribute<String> DESCRIPTION = new Attribute<>(NAMESPACE, "description", String.class);
	public static final Attribute.Set<String> TAGS = new Attribute.Set<>(NAMESPACE, "tags");
	public static final Attribute.WithDefaultValue<Boolean> HIDDEN = new Attribute.WithDefaultValue<>(NAMESPACE, "hidden",
		Boolean.class, false);
	public static final Attribute.WithDefaultValue<Boolean> ABSTRACT = new Attribute.WithDefaultValue<>(NAMESPACE, "abstract",
			Boolean.class, false);
}
