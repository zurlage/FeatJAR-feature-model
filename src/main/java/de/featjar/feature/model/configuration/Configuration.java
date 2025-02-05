/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-feature-model.
 *
 * feature-model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * feature-model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with feature-model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-feature-model> for further information.
 */
package de.featjar.feature.model.configuration;

import de.featjar.base.FeatJAR;
import de.featjar.feature.model.*;
import de.featjar.formula.*;
import de.featjar.formula.assignment.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Represents a configuration and provides operations for the configuration process.
 *
 * @author Luca zur Lage
 * @author Werner Münch
 * @author Tom Röhrig
 */
public class Configuration implements Cloneable {

    private final LinkedHashMap<String, SelectableFeature> selectableFeatures = new LinkedHashMap<>();
    private IFeatureModel featureModel;
    private SelectableFeature root;

    /**
     * This method creates a clone of the given {@link Configuration}
     *
     * @param configuration The configuration to clone
     */
    protected Configuration(Configuration configuration) {
        updateFeatures(configuration.featureModel);
        for (final SelectableFeature f : configuration.selectableFeatures.values()) {
            final SelectableFeature newFeature = getSelectableFeature(f.getName(), featureModel == null);
            if (newFeature != null) {
                setManual(newFeature, f.getManual());
                setAutomatic(newFeature, f.getAutomatic());
                newFeature.cloneProperties(f);
            }
        }
    }

    /**
     * Copy constructor. Copies the status of a given configuration.
     *
     * @param configuration the old configuration.
     * @param featureModel the underlying feature model. The model can be different from the model of the old configuration.
     */
    public Configuration(Configuration configuration, IFeatureModel featureModel) {
        updateFeatures(featureModel);
        for (final SelectableFeature oldFeature : configuration.selectableFeatures.values()) {
            final SelectableFeature newFeature = selectableFeatures.get(oldFeature.getName());
            if (newFeature != null) {
                newFeature.setManual(oldFeature.getManual());
                newFeature.setAutomatic(oldFeature.getAutomatic());
                newFeature.cloneProperties(oldFeature);
            }
        }
    }

    public Configuration() {}

    /**
     * Copy constructor. Copies the status of a given configuration.
     *
     * @param featureModel the underlying feature model. The model can be different from the model of the old configuration.
     */
    public Configuration(IFeatureModel featureModel) {
        updateFeatures(featureModel);
    }

    /**
     * Creates configuration from literal set.
     *
     * @param featureModel the underlying feature model.
     * @param booleanAssignment contains literals with truth values.
     * @param variableMap mapping of variable names to indices. Is used to link a literal index in a {@link ABooleanAssignment}.
     * @return configuration which was generated from the literal set.
     */
    public static Configuration fromLiteralSet(
            final IFeatureModel featureModel, final BooleanAssignment booleanAssignment, VariableMap variableMap) {
        final Configuration configuration = new Configuration(featureModel);

        booleanAssignment.stream().filter(literal -> literal != 0).forEach(literal -> {
            SelectableFeature selectable =
                    configuration.getSelectableFeature(variableMap.get(literal).orElseThrow());
            selectable.setManual(literal > 0 ? Selection.SELECTED : Selection.UNSELECTED);
        });

        return configuration;
    }

    /**
     * Updates the features of the underlying feature model from the configuration.
     *
     * @param featureModel new underlying featureModel for the Configuration.
     * @return true if a feature model different from the underlying feature model was passed.
     */
    public boolean updateFeatures(IFeatureModel featureModel) {
        if ((featureModel != null) && (this.featureModel != featureModel)) {
            initFeatures(featureModel);
            return true;
        }
        return false;
    }

    /**
     * Changes {@link Configuration#featureModel} and adjusts {@link Configuration#selectableFeatures} to the new featureModel.
     *
     * @param featureModel new underlying featureModel for the Configuration.
     */
    private void initFeatures(IFeatureModel featureModel) {
        final IFeature featureRoot = featureModel.getRootFeatures().get(0);
        if (featureRoot != null) {
            this.featureModel = featureModel;
            selectableFeatures.clear();
            for (final IFeature child : featureModel.getFeatures()) {
                selectableFeatures.put(child.getName().get(), new SelectableFeature(child));
            }
        }
    }

    /**
     * Sets the automatic selection status of a feature.
     *
     * @param feature the given feature.
     * @param selection the new automatic selection status for the given {@link Feature}.
     */
    public void setAutomatic(SelectableFeature feature, Selection selection) {
        feature.setAutomatic(selection);
    }

    /**
     * Sets the automatic selection status of a feature by name.
     *
     * @param name of the feature.
     * @param selection the new automatic selection status for the {@link Feature} with the given name.
     */
    public void setAutomatic(String name, Selection selection) {
        final SelectableFeature feature = getSelectableFeature(name, featureModel == null);
        if (feature == null) {
            throw new FeatureNotFoundException();
        }
        setAutomatic(feature, selection);
    }

    public IFeatureModel getFeatureModel() {
        return featureModel;
    }

    public boolean hasFeatureModel() {
        return featureModel != null;
    }

    public LinkedHashMap<String, SelectableFeature> getSelectableFeatures() {
        return selectableFeatures;
    }

    public Collection<SelectableFeature> getFeatures() {
        return Collections.unmodifiableCollection(selectableFeatures.values());
    }

    public List<SelectableFeature> getManualFeatures() {
        final List<SelectableFeature> featureList = new ArrayList<>();
        for (final SelectableFeature selectableFeature : selectableFeatures.values()) {
            if ((selectableFeature.getAutomatic() == Selection.UNDEFINED)
                    && !selectableFeature.getFeature().hasHiddenParent()) {
                featureList.add(selectableFeature);
            }
        }
        return featureList;
    }

    public List<SelectableFeature> getAutomaticFeatures() {
        final List<SelectableFeature> featureList = new ArrayList<>();
        for (final SelectableFeature selectableFeature : selectableFeatures.values()) {
            if ((selectableFeature.getAutomatic() != Selection.UNDEFINED)
                    && !selectableFeature.getFeature().hasHiddenParent()) {
                featureList.add(selectableFeature);
            }
        }
        return featureList;
    }

    public SelectableFeature getRoot() {
        return root;
    }

    public SelectableFeature getSelectableFeature(String name) {
        return getSelectableFeature(name, false);
    }

    public SelectableFeature getSelectableFeature(IFeature feature) {
        return selectableFeatures.get(feature.getName());
    }

    public SelectableFeature getSelectableFeature(String name, boolean create) {
        SelectableFeature selectableFeature = selectableFeatures.get(name);
        if (create && (selectableFeature == null)) {
            selectableFeature = new SelectableFeature(name);
            selectableFeatures.put(name, selectableFeature);
        }
        return selectableFeature;
    }

    /**
     * Get the names of all selected features.
     *
     * @return set of names of all selected features.
     */
    public Set<String> getSelectedFeatureNames() {
        final Set<String> result = new HashSet<String>();
        for (final SelectableFeature feature : selectableFeatures.values()) {
            if (feature.getSelection() == Selection.SELECTED) {
                result.add(feature.getName());
            }
        }
        return result;
    }

    /**
     * Get the names of all unselected features.
     *
     * @return set of names of all unselected features.
     */
    public Set<String> getUnselectedFeatureNames() {
        final Set<String> result = new HashSet<String>();
        for (final SelectableFeature feature : selectableFeatures.values()) {
            if (feature.getSelection() == Selection.UNSELECTED) {
                result.add(feature.getName());
            }
        }
        return result;
    }

    /**
     * Get the names of all undefined features.
     *
     * @return set of names of all undefined features.
     */
    public Set<String> getUndefinedFeatureNames() {
        final Set<String> result = new HashSet<String>();
        for (final SelectableFeature feature : selectableFeatures.values()) {
            if (feature.getSelection() == Selection.UNDEFINED) {
                result.add(feature.getName());
            }
        }
        return result;
    }

    public List<IFeature> getSelectedFeatures() {
        return getFeatures(Selection.SELECTED);
    }

    public List<IFeature> getUnSelectedFeatures() {
        return getFeatures(Selection.UNSELECTED);
    }

    public List<IFeature> getUndefinedSelectedFeatures() {
        return getFeatures(Selection.UNDEFINED);
    }

    private List<IFeature> getFeatures(final Selection selection) {
        final List<IFeature> result = new ArrayList<>();
        for (final SelectableFeature feature : selectableFeatures.values()) {
            if (feature.getSelection() == selection) {
                result.add(feature.getFeature());
            }
        }
        return result;
    }

    /**
     * Turns all automatic into manual values.
     *
     * @param discardDeselected if {@code true} all automatic deselected features get undefined instead of manual deselected.
     */
    public void makeManual(boolean discardDeselected) {
        for (final SelectableFeature feature : selectableFeatures.values()) {
            final Selection autoSelection = feature.getAutomatic();
            if (autoSelection != Selection.UNDEFINED) {
                feature.setAutomatic(Selection.UNDEFINED);
                if (!discardDeselected || (autoSelection == Selection.SELECTED)) {
                    feature.setManual(autoSelection);
                }
            }
        }
    }

    public void setManual(SelectableFeature feature, Selection selection) {
        feature.setManual(selection);
    }

    public void setManual(String name, Selection selection) {
        final SelectableFeature feature = getSelectableFeature(name, featureModel == null);
        if (feature == null) {
            throw new FeatureNotFoundException();
        }
        setManual(feature, selection);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final SelectableFeature feature : selectableFeatures.values()) {
            if ((feature.getSelection() == Selection.SELECTED)
                    && feature.getFeature().isConcrete()) {
                builder.append(feature.getFeature().getName());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Creates and returns a copy of this configuration.
     *
     * @return configuration a clone of this configuration.
     */
    @Override
    public Configuration clone() {
        if (!this.getClass().equals(Configuration.class)) {
            try {
                return (Configuration) super.clone();
            } catch (final CloneNotSupportedException e) {
                FeatJAR.log().error(e);
                throw new RuntimeException("Cloning is not supported for " + this.getClass());
            }
        }
        return new Configuration(this);
    }

    /**
     * Resets all feature selection values to undefined.
     */
    public void resetValues() {
        for (final SelectableFeature feature : selectableFeatures.values()) {
            feature.setManual(Selection.UNDEFINED);
            feature.setAutomatic(Selection.UNDEFINED);
        }
    }

    /**
     * Resets all automatic feature selection values to undefined.
     */
    public void resetAutomaticValues() {
        for (final SelectableFeature feature : selectableFeatures.values()) {
            feature.setAutomatic(Selection.UNDEFINED);
        }
    }
}
