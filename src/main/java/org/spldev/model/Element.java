package org.spldev.model;

import org.spldev.model.util.Attributable;
import org.spldev.model.util.Attribute;
import org.spldev.model.util.Identifiable;
import org.spldev.model.util.Identifier;

import java.util.*;

/**
 * Implements identification and attribute valuation. Each {@link FeatureModel}
 * and all its {@link Feature features} and {@link Constraint constraints} are
 * uniquely identified by an {@link Identifier}. Also, each element can be
 * annotated with arbirtrary {@link Attribute attributes}.
 *
 * @author Elias Kuiter
 */
public abstract class Element implements Identifiable, Attributable {
	protected final Identifier identifier;
	protected final Map<Attribute<?>, Object> attributeToValueMap = new HashMap<>();

	public Element(Identifier identifier) {
		Objects.requireNonNull(identifier);
		this.identifier = identifier;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public Map<Attribute<?>, Object> getAttributeToValueMap() {
		return attributeToValueMap;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Element that = (Element) o;
		return getIdentifier().equals(that.getIdentifier());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getIdentifier());
	}
}
