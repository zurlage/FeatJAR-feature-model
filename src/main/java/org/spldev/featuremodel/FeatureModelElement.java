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
package org.spldev.featuremodel;

import org.spldev.featuremodel.event.DefaultEventManager;
import org.spldev.featuremodel.event.FeatureIDEEvent;
import org.spldev.featuremodel.event.IEventListener;
import org.spldev.featuremodel.event.IEventManager;

import java.util.List;

/**
 * Partial implementation of feature and constraint.
 *
 * @author Sebastian Krieter
 *
 */
public abstract class FeatureModelElement implements IEventManager {
	protected final long id;

	protected String name;

	protected final FeatureModel featureModel;
	protected final IEventManager eventManager = new DefaultEventManager();

	protected FeatureModelElement(FeatureModelElement oldElement, FeatureModel featureModel) {
		this.featureModel = featureModel != null ? featureModel : oldElement.featureModel;
		id = oldElement.id;
		name = (oldElement.name == null) ? null : new String(oldElement.name);
	}

	public FeatureModelElement(FeatureModel featureModel) {
		if (featureModel == null) {
			throw new NullPointerException("Feature model must not be null!");
		}
		id = featureModel.getNextElementId();
		this.featureModel = featureModel;
		name = null;
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public final long getInternalId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public final void addListener(IEventListener listener) {
		eventManager.addListener(listener);
	}

	@Override
	public List<IEventListener> getListeners() {
		return eventManager.getListeners();
	}

	@Override
	public final void removeListener(IEventListener listener) {
		eventManager.removeListener(listener);
	}

	@Override
	public final void fireEvent(FeatureIDEEvent event) {
		eventManager.fireEvent(event);
	}

	@Override
	public final int hashCode() {
		return (int) (37 * id);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final FeatureModelElement other = (FeatureModelElement) obj;
		return id == other.id;
	}

	/**
	 * Returns the element's custom-defined properties. These properties can be get and set without changes to the code base.
	 * Custom-Properties consist of a key-value pair and can stored to the file system.
	 *
	 * @since 3.0
	 *
	 * @return Implementation-independent custom feature properties.
	 */
	abstract IPropertyContainer getCustomProperties();
}