package org.spldev.model;

import org.spldev.model.util.Attribute;
import org.spldev.model.util.Identifiable;

/**
 * Defines some useful {@link Attribute attributes} for
 * {@link org.spldev.model.FeatureModel feature models},
 * {@link org.spldev.model.Feature features}, and
 * {@link org.spldev.model.Constraint constraints}.
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
