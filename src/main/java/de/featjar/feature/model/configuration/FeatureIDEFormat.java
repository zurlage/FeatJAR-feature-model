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

import static de.featjar.feature.model.io.StringTable.DOES_NOT_EXIST;
import static de.featjar.feature.model.io.StringTable.FEATURE;
import static de.featjar.feature.model.io.StringTable.FIDECONF;
import static de.featjar.feature.model.io.StringTable.SELECTION_NOT_POSSIBLE_ON_FEATURE;
import static de.featjar.feature.model.io.StringTable.WRONG_CONFIGURATION_FORMAT;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.ParseException;
import de.featjar.base.io.input.AInputMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended configuration format for FeatureIDE projects.<br> Lists all features and indicates the manual and automatic selection.
 *
 * @author Sebastian Krieter
 */
public class FeatureIDEFormat implements IFormat<Configuration> {

    // public static final String ID = PluginID.PLUGIN_ID + ".format.config." + FeatureIDEFormat.class.getSimpleName();

    public static final String EXTENSION = FIDECONF;

    private static final String NEWLINE = System.lineSeparator();

    /**
     * Parses a String representation of a FeatureIDE Format into a Configuration.
     *
     * @param inputmapper the input mapper
     * @return Configuration inside the Result wrapper
     */
    @Override
    public Result<Configuration> parse(AInputMapper inputmapper) {

        Configuration configuration = new Configuration();
        List<Problem> warnings = new ArrayList<>();

        String line = null;
        int lineNumber = 1;
        try (BufferedReader reader = inputmapper.get().getReader(); ) {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    Selection manual = Selection.UNDEFINED, automatic = Selection.UNDEFINED;
                    try {
                        switch (Integer.parseInt(line.substring(0, 1))) {
                            case 0:
                                manual = Selection.UNSELECTED;
                                break;
                            case 1:
                                manual = Selection.SELECTED;
                                break;
                            case 2:
                                break;
                            default:
                                warnings.add(new Problem(new ParseException(WRONG_CONFIGURATION_FORMAT, lineNumber)));
                                break;
                        }
                        switch (Integer.parseInt(line.substring(1, 2))) {
                            case 0:
                                automatic = Selection.UNSELECTED;
                                break;
                            case 1:
                                automatic = Selection.SELECTED;
                                break;
                            case 2:
                                break;
                            default:
                                warnings.add(new Problem(new ParseException(WRONG_CONFIGURATION_FORMAT, lineNumber)));
                                break;
                        }
                    } catch (final NumberFormatException e) {
                        warnings.add(new Problem(new ParseException(WRONG_CONFIGURATION_FORMAT, lineNumber, e)));
                    }

                    // final String name = renamingsManager == null ? line.substring(2) :
                    // renamingsManager.getNewName(line.substring(2));
                    final String name = line.substring(2);

                    final SelectableFeature feature = configuration.getSelectableFeature(name);
                    if (feature == null) {
                        warnings.add(new Problem(new ParseException(FEATURE + name + DOES_NOT_EXIST, lineNumber)));
                    } else {
                        try {
                            configuration.setManual(feature, manual);
                            configuration.setAutomatic(feature, automatic);
                        } catch (final SelectionNotPossibleException e) {
                            warnings.add(new Problem(
                                    new ParseException(SELECTION_NOT_POSSIBLE_ON_FEATURE + name, lineNumber, e)));
                        }
                    }
                }
                lineNumber++;
            }
        } catch (final IOException e) {
            warnings.add(new Problem(e));
        }

        return Result.of(configuration, warnings);
    }

    /**
     * Returns the String representation of a Configuration in the FeatureIDE Format.
     *
     * @param configuration the object
     * @return String representation of the Configuration inside the Result wrapper
     */
    @Override
    public Result<String> serialize(Configuration configuration) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(
                "# Lists all features from the model with manual (first digit) and automatic (second digit) selection");
        buffer.append(NEWLINE);
        buffer.append("# 0 = deselected, 1 = selected, 2 = undefined");
        buffer.append(NEWLINE);

        for (final SelectableFeature feature : configuration.getFeatures()) {
            buffer.append(Integer.toString(getSelectionCode(feature.getManual())));
            // buffer.append(',');
            buffer.append(Integer.toString(getSelectionCode(feature.getAutomatic())));
            // buffer.append(',');
            buffer.append(feature.getName());
            buffer.append(NEWLINE);
        }

        return Result.of(buffer.toString());
    }

    private int getSelectionCode(Selection selection) {
        switch (selection) {
            case SELECTED:
                return 1;
            case UNDEFINED:
                return 2;
            case UNSELECTED:
                return 0;
            default:
                return 3;
        }
    }

    @Override
    public String getFileExtension() {
        return EXTENSION;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public String getName() {
        return "FeatureIDE-Internal";
    }
}
