package org.spldev.featuremodel.io;

import org.spldev.featuremodel.util.Attributable;
import org.spldev.featuremodel.util.Attribute;
import org.spldev.util.data.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helpers for parsing and writing attributes and attribute values.
 *
 * @author Elias Kuiter
 */
public class AttributeIO {
	public static Optional<Class<?>> getType(String typeString) {
		switch (typeString.toLowerCase()) {
			case "string":
				return Optional.of(String.class);
			case "bool":
			case "boolean":
				return Optional.of(Boolean.class);
			case "int":
			case "integer":
				return Optional.of(Integer.class);
			case "long":
				return Optional.of(Long.class);
			case "float":
				return Optional.of(Float.class);
			case "double":
				return Optional.of(Double.class);
		}
		return Optional.empty();
	}

	public static Optional<String> getTypeString(Class<?> type) {
		if (String.class.equals(type)) {
			return Optional.of("string");
		} else if (Boolean.class.equals(type)) {
			return Optional.of("boolean");
		} else if (Integer.class.equals(type)) {
			return Optional.of("integer");
		} else if (Long.class.equals(type)) {
			return Optional.of("long");
		} else if (Float.class.equals(type)) {
			return Optional.of("float");
		} else if (Double.class.equals(type)) {
			return Optional.of("double");
		}
		return Optional.empty();
	}

	public static Optional<Attribute<?>> parseAttribute(String namespace, String name, String typeString) {
		return getType(typeString).map(type -> new Attribute<>(namespace, name, type));
	}

	public static Optional<Object> parseAttributeValue(Class<?> type, String valueString) {
		if (String.class.equals(type)) {
			return Optional.of(valueString);
		} else if (Boolean.class.equals(type)) {
			return Optional.of(Boolean.valueOf(valueString));
		} else if (Integer.class.equals(type)) {
			return Optional.of(Integer.valueOf(valueString));
		} else if (Long.class.equals(type)) {
			return Optional.of(Long.valueOf(valueString));
		} else if (Float.class.equals(type)) {
			return Optional.of(Float.valueOf(valueString));
		} else if (Double.class.equals(type)) {
			return Optional.of(Double.valueOf(valueString));
		}
		return Optional.empty();
	}

	public static Optional<Object> parseAttributeValue(String typeString, String valueString) {
		return getType(typeString).map(type -> parseAttributeValue(type, valueString));
	}

	public static List<Problem> parseAndSetAttributeValue(Attributable attributable, String namespace, String name, String typeString, String valueString) {
		List<Problem> problems = new ArrayList<>();
		Optional<Attribute<?>> attribute = AttributeIO.parseAttribute(namespace, name, typeString);
		Optional<?> value = parseAttributeValue(typeString, valueString);
		if (attribute.isEmpty()) {
			problems.add(new Problem("invalid type for attribute " + name, Problem.Severity.WARNING));
		} else if (value.isEmpty()) {
			problems.add(new Problem("invalid value for attribute " + name, Problem.Severity.WARNING));
		} else if (attributable.hasAttributeValue(attribute.get())) {
			problems.add(new Problem("already has value for attribute " + name, Problem.Severity.WARNING));
		} else {
			((Attributable.Mutator<Attributable>) () -> attributable).setArbitraryAttributeValue(attribute.get(), value.get());
		}
		return problems;
	}
}
