package org.spldev.featuremodel.util;

import java.util.function.Consumer;

/**
 * An object that can be analyzed with an {@link Analyzer}.
 * todo Analysis results are cached when o.analyze() is used. Mutation may invalidate analysis results.
 *
 * @param <T> the type of the analyzable object
 * @param <U> the type of the analyzer object
 * @author Elias Kuiter
 */
public interface Analyzable<T, U extends Analyzable.Analyzer<T>> {
    interface Analyzer<T> {
        T getAnalyzable();
    }

    U getAnalyzer();

    void setAnalyzer(U analyzer);

    default U analyze() {
        return getAnalyzer();
    }

    default T analyze(Consumer<U> analyzerConsumer) {
        analyzerConsumer.accept(getAnalyzer());
        return getAnalyzer().getAnalyzable();
    }

}
