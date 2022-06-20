/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.spldev.featuremodel.io;

import org.spldev.featuremodel.Constraint;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureTree;
import org.spldev.formula.io.textual.NodeWriter;
import org.spldev.util.io.format.Format;
import org.spldev.util.io.format.InputHeader;

/**
 * Writes feature models in an internal, simplified format.
 *
 * @author Sebastian Krieter
 */
public class InternalFeatureModelFormat implements Format<FeatureModel> {
	private static final String[] SYMBOLS = { "!", "&&", "||", "->", "<->", ", ", "choose", "atleast", "atmost" };
	private static final String NEWLINE = System.getProperty("line.separator", "\n");

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public String serialize(FeatureModel object) {
		final StringBuilder sb = new StringBuilder();
		final FeatureTree root = object.getFeatureTree();
		if (root == null) {
			return "";
		}
		sb.delete(0, sb.length());

		sb.append(root.getFeature().getName());
		sb.append("{");
		sb.append(NEWLINE);

		writeFeatureGroup(root, sb);

		for (final Constraint constraint : object.getConstraints()) {
			sb.append(new NodeWriter().write(constraint.getFormula())); // todo use SYMBOLS
			sb.append(NEWLINE);
		}

		sb.append("}");

		return sb.toString();
	}

	private void writeFeatureGroup(FeatureTree root, StringBuilder sb) {
		if (root.isAnd()) {
			for (final FeatureTree feature : root.getChildren()) {
				writeFeature(feature, sb);
			}
		} else if (root.isOr()) {
			sb.append("o{");
			sb.append(NEWLINE);
			for (final FeatureTree feature : root.getChildren()) {
				writeFeature(feature, sb);
			}
			sb.append("}");
			sb.append(NEWLINE);
		} else if (root.isAlternative()) {
			sb.append("x{");
			sb.append(NEWLINE);
			for (final FeatureTree f : root.getChildren()) {
				writeFeature(f, sb);
			}
			sb.append("}");
			sb.append(NEWLINE);
		}
	}

	private void writeFeature(FeatureTree feature, StringBuilder sb) {
		if (feature.getFeature().isAbstract()) {
			sb.append("a ");
		}
		if (feature.isMandatory() && (!feature.hasParent() || feature.getParent().get().isAnd())) {
			sb.append("m ");
		}
		sb.append(feature.getFeature().getName());
		final String description = feature.getFeature().getDescription().orElse(null);
		final boolean hasDescription = (description != null) && !description.isEmpty();

		if ((feature.getNumberOfChildren() != 0) || hasDescription) {
			sb.append(" {");
			sb.append(NEWLINE);
			if (hasDescription) {
				sb.append("d\"");
				sb.append(description.replace("\"", "\\\""));
				sb.append("\";");
				sb.append(NEWLINE);
			}

			writeFeatureGroup(feature, sb);

			sb.append("}");
		}
		sb.append(NEWLINE);
	}

	@Override
	public boolean supportsContent(InputHeader inputHeader) {
		return true;
	}

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public InternalFeatureModelFormat getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return "FeatureIDE Internal";
	}
}
