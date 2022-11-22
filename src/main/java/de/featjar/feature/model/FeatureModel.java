/*
 * Copyright (C) 2022 Elias Kuiter
 *
 * This file is part of model.
 *
 * model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import de.featjar.base.data.*;
import de.featjar.feature.model.mixins.*;

import java.util.*;

/**
 * A feature model represents the configuration space of a software system.
 * We equate feature models with feature diagrams
 * (i.e., a {@link FeatureTree} labeled with features and a list of {@link Constraint constraints}).
 * For safe mutation, rely only on the methods of {@link Mutable}.
 *
 * @author Elias Kuiter
 */
public class FeatureModel extends Element // TODO : IFeatureModel, IFeature
        implements FeatureModelFeatureTreeMixin,
        FeatureModelConstraintMixin,
        FeatureModelFeatureOrderMixin,
        CommonAttributesMixin,
        FeatureModelCacheMixin,
                Mutable<FeatureModel, FeatureModel.Mutator>,
        Analyzable<FeatureModel, FeatureModel.Analyzer> {
    //protected final Store store;

    //TODO put flattened fm into store (maybe dispatch mutators of flattened model to original models)
    //TODO store<...>, formulacomputation, decision propagation...?
    //TODO store in analyzer, apply(input,monitor,store) zum default machen(?)

    //TODO: we allow all kinds of modeling constructs, but not all analyses/computations support all constructs.
    //e.g., multiplicities are difficult to map to SAT. somehow, this should be checked.
    // maybe store required/incompatible capabilities for computations? eg., incompatible with Plaisted-Greenbaum/multiplicities/...?
    //and then implement different alternative algorithms with different capabilities.
    //maybe this could be encoded first-class as a feature model.
    //this could even be used to generate query plans (e.g., find some configuration that counts my formula).
    //every plugin defines a feature model (uvl) that restricts what its extensions can and cannot do (replacing extensions.xml)

    protected final FeatureModelTree featureModelTree;
    protected final FeatureTree featureTree;
    protected final List<Constraint> constraints = Collections.synchronizedList(new ArrayList<>());
    protected FeatureOrder featureOrder = FeatureOrder.ofPreOrder();
    protected final Map<Identifier, Element> elementCache = Collections.synchronizedMap(new LinkedHashMap<>());
    //TODO elementcache -> store? computation?
    protected final Set<Feature> featureCache = Collections.synchronizedSet(new HashSet<>());
    protected Mutator mutator;
    protected Analyzer analyzer;

    public FeatureModel(Identifier identifier) {
        super(identifier);
        featureModelTree = new FeatureModelTree(this);
        final Feature root = new Feature(this);
        featureTree = root.getFeatureTree();
        finishInternalMutation();
    }

    @Override
    public FeatureModel getFeatureModel() {
        return this;
    }

    public FeatureModelTree getFeatureModelTree() {
        return featureModelTree;
    }

    @Override
    public FeatureTree getFeatureTree() {
        return featureTree;
    }

    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public FeatureOrder getFeatureOrder() {
        return featureOrder;
    }

    @Override
    public Map<Identifier, Element> getElementCache() {
        return elementCache;
    }

    @Override
    public Set<Feature> getFeatureCache() {
        return featureCache;
    }

    @Override
    public Mutator getMutator() {
        return mutator == null ? (mutator = new Mutator()) : mutator;
    }

    @Override
    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer == null ? (analyzer = new Analyzer()) : analyzer;
    }

    @Override
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public void finishInternalMutation() {
        FeatureModelCacheMixin.super.finishInternalMutation();
    }

    @Override
    public String toString() {
        return String.format("FeatureModel{features=%s, constraints=%s}", getFeatures(), constraints);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(); // TODO
    }

    public class Mutator
            implements de.featjar.base.data.Mutator<FeatureModel>,
                    FeatureModelFeatureTreeMixin.Mutator,
                    FeatureModelConstraintMixin.Mutator,
                    FeatureModelFeatureOrderMixin.Mutator,
                    CommonAttributesMixin.Mutator<FeatureModel>,
                    FeatureModelCacheMixin.Mutator {
        @Override
        public FeatureModel getMutable() {
            return FeatureModel.this;
        }

        @Override
        public void setFeatureOrder(FeatureOrder featureOrder) {
            FeatureModel.this.featureOrder = featureOrder;
        }
    }

    public class Analyzer
            implements de.featjar.base.data.Analyzer<FeatureModel>,
                    FeatureModelFeatureTreeMixin.Analyzer,
                    FeatureModelConstraintMixin.Analyzer {
        @Override
        public FeatureModel getAnalyzable() {
            return FeatureModel.this;
        }
    }
}
