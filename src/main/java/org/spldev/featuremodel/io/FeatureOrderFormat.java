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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureTree;
import org.spldev.util.data.Result;
import org.spldev.util.io.format.Format;
import org.spldev.util.io.format.Input;
import org.spldev.util.tree.Trees;

/**
 * Reads / Writes a feature order file.
 *
 * @author Sebastian Krieter
 */
public class FeatureOrderFormat implements Format<FeatureModel> {
	@Override
	public Result<FeatureModel> parse(Input source) {
		// todo: create trivial feature model with 2^n products
		return Result.empty();
	}

	@Override
	public Result<FeatureModel> parse(Input source, Supplier<FeatureModel> supplier) {
		return parse(source, supplier.get());
	}

	private Result<FeatureModel> parse(Input source, FeatureModel featureModel) {
		final String[] lines = source.toString().split("[\n|\r]+");
		featureModel.setFeatureOrderList(Arrays.asList(lines));
		return Result.of(featureModel);
	}

	@Override
	public String serialize(FeatureModel object) {
		final String newLine = System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();

		sb.append(((object.isFeatureOrderUserDefined()) ? "true" : "false") + newLine);

		Collection<String> list = object.getFeatureOrderList();
		if (list.isEmpty()) {
			list = Trees.preOrderStream(object.getStructure().getRoot())
				.filter(FeatureTree::isConcrete)
				.map(FeatureTree::getFeature)
				.map(Feature::getName)
				.collect(Collectors.toList());
		}

		for (final String featureName : list) {
			sb.append(featureName);
			sb.append(newLine);
		}

		return sb.toString();
	}

	@Override
	public String getFileExtension() {
		return "order";
	}

	@Override
	public FeatureOrderFormat getInstance() {
		return this;
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
		return "Feature Order";
	}
}
