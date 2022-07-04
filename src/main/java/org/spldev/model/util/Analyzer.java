package org.spldev.model.util;

/**
 * An object that can analyze an {@link Analyzable}.
 *
 * @param <T> the type of the analyzable object
 * @author Elias Kuiter
 */
public interface Analyzer<T> {
	T getAnalyzable();
}
