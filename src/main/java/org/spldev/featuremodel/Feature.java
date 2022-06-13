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

import org.spldev.featuremodel.event.FeatureIDEEvent;
import org.spldev.featuremodel.impl.*;

/**
 * The feature interface represents any class that acts in the sense of a <i>feature</i> in FeatureIDE. A feature contains both <ul> <li>certain fixed
 * properties (e.g., its name) which are available by the features implementation of {@link org.spldev.featuremodel.IFeatureProperty IFeatureProperty},
 * and</li> <li>custom properties which can be stored and received in a key-value store like fashion without the need of modifying existing code</li> </ul> Each
 * feature belongs to other features where statements between features form a <i>feature model</i>. Hence, a feature is related to a <i>structure</i> which
 * connects the feature to, e.g., it's <i>children</i>, or it's <i>parent</i> feature in terms of these statements. Any feature is highly related to it's name
 * and identified by it's internal <i>identifier</i>. The last both properties are mixed-in with the {@link FeatureModelElement} interface. An instance of
 * <code>IFeature</code> intended to be instantiated by a {@link IFeatureModelFactory}. <br> <br> FeatureIDE provides an adapter implementation
 * {@link org.spldev.featuremodel.impl.AFeature AFeature} which is a abstract base class and which should be prefered as starting point for custom
 * implementations. This base class contains ready-to-use implementations for both <code>IFeature</code> and {@link FeatureModelElement}. <br> <br>
 * <b>Example</b><br> The following example demonstrate the creation of a new feature called <i>FeatureA</i> using FeatureIDE's default <code>IFeature</code>
 * implementation {@link org.spldev.featuremodel.impl.Feature Feature}, and the corresponding default factory
 * {@link org.spldev.featuremodel.impl.DefaultFeatureModelFactory DefaultFeatureModelFactory} over the conviennent factory class
 * {@link FMFactoryManager}: <code> IFeatureModel model = FMFactoryManager.getFactory().createFeatureModel(); IFeature feature =
 * FMFactoryManager.getFactory().createFeature(model, "FeatureA"); </code> A unified handling of certain <code>IFeature</code> implementations (in terms of
 * conviennent methods) can be achieved with the use of {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} helper class. <br> <br> <b>API
 * notes</b>: The classes internal structure has heavily changed compared to older FeatureIDE version. A bridge to the old-fashioned handling is available in
 * {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} as static methods. <br> <br> <b>Notes on equals method</b>: Any implementation have to
 * provide a {@link Object#equals(Object)} implementation when the feature implementation should be fully useable in the FeatureIDE system (and therefore, have
 * to be an instance of {@link FeatureModelElement}), which at least returns <b>true</b> when the internal identifier of two features are the same, and
 * otherwise <b>false</b>. <br> <br> <b>Compatibility Notes</b>: To provide compatibility to earlier versions of FeatureIDE, the <i>out-dated</i> class
 * {@link org.spldev.featuremodel.impl.Feature Feature} also implements the <code>IFeature</code> interface. Developers should neither use nor extend this obsolete class since it is
 * deprecated and will be removed in one of the next versions.
 *
 * @see org.spldev.featuremodel.impl.AFeature Default implementation of <code>IFeature</code> (as starting point for custom implementations)
 *
 * @see Constraint Interface for feature constraints (<code>IConstraint</code>)
 * @see FeatureModel Interface for feature models (<code>IFeatureModel</code>)
 * @see IFeatureProperty Interface for feature properties (<code>IFeatureProperty</code>)
 * @see FeatureStructure Interface for a feature's structure (<code>IFeatureStructure</code>)
 *
 * @see org.spldev.featuremodel.impl.AConstraint Default implementation for feature constraints (<code>AConstraint</code>)
 * @see org.spldev.featuremodel.impl.FeatureModel Default implementation for feature models (<code>FeatureModel</code>)
 * @see org.spldev.featuremodel.impl.FeatureProperty Default implementation for feature properties (<code>FeatureProperty</code>)
 * @see org.spldev.featuremodel.impl.FeatureStructure Default implementation for a feature's structure (<code>FeatureStructure</code>)
 *
 * @since 3.0
 *
 * @author Sebastian Krieter
 * @author Marcus Pinnecke

 * Implementation of {@link AFeature} used as default implementation inside FeatureIDE. <br> <br> This class implements the functionality required by
 * {@link org.spldev.featuremodel.Feature} and a {@link AFeatureModelElement}, specified in {@link AFeature}. <br> <br> This class is intended to be the default implementation for
 * regular use-cases of feature management. Further specialization for other use-cases is available in the sub classes {@link MultiFeature} and inside
 * {@link SXFMFormat}. <br> <br> An instance of a <code>Feature</code> is intended to be instantiated by a {@link IFeatureModelFactory}. <br> <br>
 * <b>Example</b><br> The following example demonstrate the creation of a new feature called <i>FeatureA</i> using FeatureIDE's default <code>IFeature</code> (
 * <code>AFeature</code>) implementation {@link org.spldev.featuremodel.impl.Feature Feature}, and the corresponding default factory
 * {@link org.spldev.featuremodel.impl.DefaultFeatureModelFactory DefaultFeatureModelFactory} over the convenient factory class
 * {@link FMFactoryManager}. The instance is stored against the <code>IFeature</code> interface: <code> IFeatureModel model =
 * FMFactoryManager.getFactory().createFeatureModel(); IFeature feature = FMFactoryManager.getFactory().createFeature(model, "FeatureA"); </code> A unified
 * handling of certain <code>Feature</code> (<code>AFeature</code>, <code>IFeature</code>) implementations (in terms of conviennent methods) can be achieved
 * with the use of {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} helper class.
 *
 * @see org.spldev.featuremodel.impl.AFeature Default implementation of the interface for feature in FeatureIDE (<code>AFeature</code>)
 *
 * @see Constraint Interface for feature constraints (<code>IConstraint</code>)
 * @see FeatureModel Interface for feature models (<code>IFeatureModel</code>)
 * @see IFeatureProperty Interface for feature properties (<code>IFeatureProperty</code>)
 * @see FeatureStructure Interface for a feature's structure (<code>IFeatureStructure</code>)
 *
 * @see org.spldev.featuremodel.impl.AConstraint Default implementation for feature constraints (<code>AConstraint</code>)
 * @see org.spldev.featuremodel.impl.FeatureModel Default implementation for feature models (<code>FeatureModel</code>)
 * @see org.spldev.featuremodel.impl.FeatureProperty Default implementation for feature properties (<code>FeatureProperty</code>)
 * @see org.spldev.featuremodel.impl.FeatureStructure Default implementation for a feature's structure (<code>FeatureStructure</code>)
 *
 * FeatureIDE's default {@link org.spldev.featuremodel.Feature} implementation as the starting point for user-defined implementations. <br> <br> This class implements the minimum
 * functionality required for a {@link org.spldev.featuremodel.Feature} class and further functionality from {@link AFeatureModelElement}. The first provides getter and setter of the
 * feature's <i>properties</i>, <i>custom properties</i>, and the feature's <i>structure</i> (for more information, see {@link org.spldev.featuremodel.Feature} documentation). The
 * second implements FeatureIDE's internal functionality to identify a given feature inside a feature model, and to provide event listening capabilities with
 * the listener/observer pattern. <br> <br> This class is intended to be a starting point for user-defined implementation, such that a subclass of
 * <code>AFeature</code> only have to provide an implementation of {@link org.spldev.featuremodel.Feature#clone(FeatureModel, FeatureStructure)}. FeatureIDE provides a default
 * non-abstract implementation {@link org.spldev.featuremodel.impl.Feature Feature} which extends <code>AFeature</code> in this sense. <br> <br> An
 * instance of a subclass of <code>AFeature</code> is intended to be instantiated by a {@link IFeatureModelFactory}. <br> <br> <b>Example</b><br> The following
 * example demonstrate the creation of a new feature called <i>FeatureA</i> using FeatureIDE's default <code>AFeature</code> implementation
 * {@link org.spldev.featuremodel.impl.Feature Feature}, and the corresponding default factory
 * {@link org.spldev.featuremodel.impl.DefaultFeatureModelFactory DefaultFeatureModelFactory} over the conviennent factory class
 * {@link FMFactoryManager}. The instance is stored against the <code>IFeature</code> interface: <code> IFeatureModel model =
 * FMFactoryManager.getFactory().createFeatureModel(); IFeature feature = FMFactoryManager.getFactory().createFeature(model, "FeatureA"); </code> A unified
 * handling of certain <code>AFeature</code> (<code>IFeature</code>) implementations (in terms of conviennent methods) can be achieved with the use of
 * {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} helper class. <br> <br> <b>Notes on equals method</b>: The <code>AFeature</code>
 * implementation inherits the {@link Object#equals(Object)} capability from {@link AFeatureModelElement}. The feature equality is defined as the equality of
 * the underlying internal identifiers per feature. <br> <br>
 *
 * @see org.spldev.featuremodel.Feature Interface for feature in FeatureIDE (<code>IFeature</code>)
 *
 * @see Constraint Interface for feature constraints (<code>IConstraint</code>)
 * @see FeatureModel Interface for feature models (<code>IFeatureModel</code>)
 * @see IFeatureProperty Interface for feature properties (<code>IFeatureProperty</code>)
 * @see FeatureStructure Interface for a feature's structure (<code>IFeatureStructure</code>)
 *
 * @see org.spldev.featuremodel.impl.AConstraint Default implementation for feature constraints (<code>AConstraint</code>)
 * @see org.spldev.featuremodel.impl.FeatureModel Default implementation for feature models (<code>FeatureModel</code>)
 * @see org.spldev.featuremodel.impl.FeatureProperty Default implementation for feature properties (<code>FeatureProperty</code>)
 * @see org.spldev.featuremodel.impl.FeatureStructure Default implementation for a feature's structure (<code>FeatureStructure</code>)
 *
 * @since 3.0
 *
 * @author Thomas Thuem
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 */
public class Feature extends FeatureModelElement {
	protected final IFeatureProperty property;
	protected final FeatureStructure structure;
	protected final IPropertyContainer propertyContainer;

	/**
	 * <b>Copy constructor</b>. Constructs a new instance of <code>Feature</code> given another feature <code>oldFeature</code>, a feature model
	 * <code>featureModel</code>, and a feature structure <code>newFeatureStructure</code> (for further information on feature model and structure, see
	 * {@link org.spldev.featuremodel.Feature} and {@link FeatureModel}). Moreover, the user-defined properties are copied. <br> <br> <b>Note</b>: The parameter
	 * <code>oldFeature</code> have to be non-null. The getter {@link AFeatureModelElement#getName()} of <code>oldFeature</code> (as an subclass of
	 * {@link AFeatureModelElement} can be <b>null</b>.
	 *
	 * @param oldFeature used to copy the original feature's identifier, and the original feature's name (if available)
	 * @param featureModel is used to set the new feature's feature model if <code>featureModel</code> is non-null. If <code>featureModel</code> is <b>null</b>,
	 *        a reference to the feature model of <code>oldFeature</code> will be used.
	 * @param newFeatrureStructure is used to set the new feature's feature structure if <code>newFeatrureStructure</code> is non-null. If
	 *        <code>newFeatrureStructure</code> is <b>null</b>, a reference to the feature structure <code>oldFeature</code> will be used.
	 *
	 * @since 3.0
	 */
	protected Feature(Feature oldFeature, FeatureModel featureModel, FeatureStructure newFeatrureStructure) {
		super(oldFeature, featureModel);

		property = oldFeature.property.clone(this);
		structure = newFeatrureStructure != null ? newFeatrureStructure : oldFeature.structure;
		propertyContainer = clonePropertyContainer(oldFeature);
	}

	/**
	 * <b>Default constructor</b>. Constructs a new instance of <code>AFeature</code> with the name <code>name</code> in a given feature model
	 * <code>featureModel</code>. The parameter <code>featureModel</code> have to be non-null since features are identified by their internal numerical
	 * identifier and <code>featureModel</code> have to provide the next free identifier.
	 *
	 * @param featureModel in which the new instance feature should be part of
	 * @param name the name of the feature.
	 *
	 * @since 3.0
	 */
	public Feature(FeatureModel featureModel, String name) {
		super(featureModel);
		this.name = name;

		property = createProperty();
		structure = createStructure();
		propertyContainer = createPropertyContainer();
	}

	protected IPropertyContainer createPropertyContainer() {
		return new MapPropertyContainer();
	}

	protected IPropertyContainer clonePropertyContainer(Feature other) {
		return new MapPropertyContainer(other.propertyContainer);
	}

	protected IFeatureProperty createProperty() {
		return new FeatureProperty(this);
	}

	protected FeatureStructure createStructure() {
		return new org.spldev.featuremodel.impl.FeatureStructure(this);
	}

	/**
	 * Returns the feature's properties. These properties depend on the {@link IFeatureProperty} implementation, but contain at least getters and setters for
	 * <ul> <li>a custom-defined description of the feature</li> <li>a string-representation of the feature which is intended for display purposes</li>
	 * <li>information about the feature status (dead, false-optional,...)</li> <li>the state of the feature's selection in the GUI</li> </ul> For user-defined
	 * properties, the {@link IFeatureProperty} implementation must be changed, or the method {@link #getCustomProperties()} can be used.
	 *
	 * @since 3.0
	 *
	 * @return Implementation-specific feature properties.
	 */
	public IFeatureProperty getProperty() {
		return property;
	}

	/**
	 * Returns the feature structure, in which this feature lives in. The structure gives information about (and setter to) the children and the parent of this
	 * feature, and statement-related properties such as if this feature is part of an alternative group, or if it is abstract or hidden. <br> <br> <b>Note</b>:
	 * The returned object have to be non-null.
	 *
	 * @since 3.0
	 *
	 * @return The features structure properties.
	 */
	public FeatureStructure getStructure() {
		return structure;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		final String oldName = this.name;
		super.setName(name);
		fireEvent(new FeatureIDEEvent(getFeatureModel(), FeatureIDEEvent.EventType.FEATURE_NAME_CHANGED, oldName, name));
	}

	@Override
	public IPropertyContainer getCustomProperties() {
		return propertyContainer;
	}

	@Override
	public String toString() {
		return getName();
	}

	/*
	 * Creates the tooltip for the feature. Used to have the same tooltip in the different views that are used within the project. The tooltip created for the
	 * feature depents on the parameter objects given to it.
	 *
	 * @param objects Objects to determine which content should be generated.
	 * @return Tooltip content as string
	 */
	/*
	public String createTooltip(Object... objects) {
		final StringBuilder toolTip = new StringBuilder();
		final IFeatureStructure structure = getStructure();
		toolTip.append(structure.isConcrete() ? CONCRETE : TOOLTIP_ABSTRACT);

		if (structure.hasHiddenParent()) {
			toolTip.append(structure.isHidden() ? TOOLTIP_HIDDEN : INHERITED_HIDDEN);
		}

		toolTip.append(structure.isRoot() ? ROOT : TOOLTIP_FEATURE);

		if (getProperty().isImplicit()) {
			toolTip.append(TOOLTIP_IMPLICIT);
		}

		// Handle analysis results if available
		if ((objects.length > 0) && (objects[0] instanceof AnalysesCollection)) {
			final AnalysesCollection collection = (AnalysesCollection) objects[0];

			final FeatureModelProperties properties = collection.getFeatureModelProperties();
			if (properties.hasStatus(FeatureModelStatus.VOID)) {
				toolTip.setLength(0);
				toolTip.trimToSize();
				toolTip.append(FEATURE_MODEL_IS_VOID);
			} else {
				final FeatureProperties featureProperties = collection.getFeatureProperty(this);
				if (featureProperties.hasStatus(FeatureStatus.DEAD)) {
					toolTip.append(IS_DEAD);
				} else if (featureProperties.hasStatus(FeatureStatus.FALSE_OPTIONAL)) {
					toolTip.append(IS_FALSE_OPTIONAL);
				} else if (featureProperties.hasStatus(FeatureStatus.INDETERMINATE_HIDDEN)) {
					toolTip.append(IS_HIDDEN_AND_INDETERMINATE);
				}
			}
		}

		final String description = getProperty().getDescription();
		if ((description != null) && !description.trim().isEmpty()) {
			toolTip.append("\n\nDescription:\n");
			toolTip.append(description);
		}

		final String contraints = FeatureUtils.getRelevantConstraintsString(this);
		if (!contraints.isEmpty()) {
			toolTip.append("\n\nConstraints:\n");
			toolTip.append(contraints);
		}

		return toolTip.toString();
	}*/


	/**
	 * Creates a new instance (new reference) of this feature with the same feature name and internal id, optionally change the features feature model and/or
	 * structure.
	 *
	 * @since 3.0
	 *
	 * @param newFeatureModel A new feature model in which the feature should be part of. If this parameter is <code>null</code>, the cloned feature's new
	 *        feature model have to be the originals feature's feature model.
	 * @param newStructure A new structure in which the feature should life. If this parameter is <code>null</code>, the cloned feature's new structure have to
	 *        be the originals feature's structure.
	 *
	 * @since 3.0
	 *
	 * @return New instance <code>f'</code> of this feature <code>f</code> such that <code>f != f'</code> but <code>f.equals(f')</code> holds.
	 */
	public Feature clone(FeatureModel newFeatureModel, FeatureStructure newStructure) {
		return new Feature(this, newFeatureModel, newStructure);
	}
}
