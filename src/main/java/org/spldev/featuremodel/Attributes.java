package org.spldev.featuremodel;

public class Attributes {
    public static final Attribute.WithDefaultValue<String> NAME = new Attribute.WithDefaultValue<>(
            "name", identifiable -> "@" + ((Identifiable) identifiable).getIdentifier().toString());
    public static final Attribute<String> DESCRIPTION = new Attribute<>("description");
    public static final Attribute.WithDefaultValue<Boolean> HIDDEN = new Attribute.WithDefaultValue<>("hidden", false);
    public static final Attribute.WithDefaultValue<Boolean> ABSTRACT = new Attribute.WithDefaultValue<>("abstract", false);
}
