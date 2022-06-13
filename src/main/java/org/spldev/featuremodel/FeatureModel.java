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
import org.spldev.featuremodel.impl.FMFactoryManager;
import org.spldev.featuremodel.impl.FeatureModelProperty;
import org.spldev.featuremodel.impl.ModelFileIdMap;
import org.spldev.formula.io.textual.NodeWriter;
import org.spldev.util.tree.Trees;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The feature model interface represents any class that acts in the sense of a <i>feature model</i> in FeatureIDE. <br> <br> A feature model contains of a
 * modifiable collection of <ul> <li>{@link Feature Features}, and</li> <li>{@link Constraint
 * Constraints of features}</li> </ul> <br> Each <i>feature</i> in a feature model has a unique identifier and is related to other features over some
 * expressions and relations which forms the feature model (such as parent-children relation with implication expression from one feature to another).
 * Additional to the structure of features (see {@link Feature} and {@link org.spldev.featuremodel.FeatureTree}
 * for more details) inside the feature models tree, features relationships can be further expressed using <i>constraints</i>. While the feature structure is
 * bound to the actual feature model tree, constraints can be state restrictions and relations to arbitrary features inside the feature model. <br> <br>
 * Features inside a feature model can by analyzed in order to determine feature properties which are implicated by the structure, the statements, and the
 * constraints. As a result of such an analysis, a set of <i>dead features</i> can be found for instance. For more information about analysis, see
 * {@link de.ovgu.featureide.fm.core.FeatureModelAnalyzer FeatureModelAnalyzer}. <br> <br> Additional to the collection mentioned above, the feature model
 * contains properties to express <ul> <li>Annotations</li> <li>Comments</li> <li>Feature Orders</li> </ul> <br> A feature model is moreover related to it's
 * project, such that the <i>project's name</i> can be received, the related composer extension can be received, as well as certain event handling logic (such
 * as model data change event handling) can be made. Furthermore, each feature model is <i>required to has an own unique system-wide identifier</i> (at least
 * during runtime). Any implementation of this interface has to provide the corresponding {@link #getId()} method and have to implement the management of
 * identifiers among a set of feature models. <br> <br> Any feature model is intended to be instantiated by a corresponding factory, the implementation-specific
 * {@link IFeatureModelFactory}. <br> <br> FeatureIDE provides a default implementation {@link org.spldev.featuremodel.impl.FeatureModel} which is used
 * for default use-cases and can be customized via inheritance of {@link org.spldev.featuremodel.impl.FeatureModel} and a user-defined
 * {@link IFeatureModelFactory feature model factory}. Internally, a feature model is represented by an unique numeric identifier, which should be considered in
 * the related {@link IFeatureModelFactory} in order to avoid confusion with other models. <br> <br> <b>Example</b><br> The following example demonstrate the
 * creation of a new feature model using FeatureIDE's default <code>FeatureModel</code> implementation {@link org.spldev.featuremodel.impl.FeatureModel
 * FeatureModel}, and the corresponding default factory {@link org.spldev.featuremodel.impl.DefaultFeatureModelFactory DefaultFeatureModelFactory} over
 * the conviennent factory class {@link FMFactoryManager}: <code> IFeatureModel model = FMFactoryManager.getFactory().createFeatureModel(); </code> A unified
 * handling of certain <code>IFeature</code>, and <code>IFeatureModel</code> implementations (in terms of conviennent methods) can be achieved with the use of
 * {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} helper class. <br> <br> <b>Caching notes</b>: A feature model implementation using the
 * <code>IFeatureModel</code> interface has to provide a map of feature names to the corresponding feature objects, the <i>feature table</i>. This data
 * structure is used in the {@link RenamingsManager} for instance. If the implementation utilizes this data structure for internal use, modifications to this
 * data structure must be protected against concurrent accesses. The default implementations {@link FeatureModel} uses a <code>ConcurrentHashMap</code> for
 * this purpose. <br> <br> <b>API notes</b>: The classes internal structure has heavily changed compared to older FeatureIDE version. A bridge to the
 * old-fashioned handling is available in {@link org.spldev.featuremodel.FeatureUtils FeatureUtils} as static methods. <br> <br> <b>Notes on thread
 * safeness</b>: At least the management of <code>IFeature</code> and <code>IFeatureModel</code> identifiers (e.g., getting the next free id) have to be thread
 * safe. The reference default implementation for feature models is <code> private static long NEXT_ID = 0;
 *
 * protected static final synchronized long getNextId() { return NEXT_ID++; } </code> <br> <br> <b>Compatibility Notes</b>: To provide compatibility to earlier
 * versions of FeatureIDE, the <i>out-dated</i> class {@link FeatureModel FeatureModel} is now a wrapper to an <code>IFeatureModel</code> instance (but
 * incompatible to it) and make use of convert-functionalities inside {@link org.spldev.featuremodel.FeatureUtils FeatureUtils}.
 *
 * @see org.spldev.featuremodel.impl.FeatureModel Default implementation of <code>IFeatureModel</code> (as starting point for custom implementations)
 *
 * @see Feature Interface for features (<code>IFeature</code>)
 * @see Constraint Interface for feature constraints (<code>IConstraint</code>)
 * @see IFeatureProperty Interface for feature properties (<code>IFeatureProperty</code>)
 * @see IFeatureStructure Interface for a feature's structure (<code>IFeatureStructure</code>)
 *
 * @see org.spldev.featuremodel.impl.Feature Default implementation for features (<code>Feature</code>)
 * @see org.spldev.featuremodel.impl.AConstraint Default implementation for feature constraints (<code>AConstraint</code>)
 * @see org.spldev.featuremodel.impl.FeatureProperty Default implementation for feature properties (<code>FeatureProperty</code>)
 * @see org.spldev.featuremodel.impl.FeatureStructure Default implementation for a feature's structure (<code>FeatureStructure</code>)
 *
 * @since 3.0
 *
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 */
/**
 * The model representation of the feature tree that notifies listeners of changes in the tree.
 *
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Marcus Pinnecke
 */
public class FeatureModel implements Cloneable, IEventManager {
	private static long NEXT_ID = 0;

	protected static final synchronized long getNextId() {
		return NEXT_ID++;
	}

	protected long id;

	private long nextElementId = 0;

	/**
	 * Feature models are identified with their system-wide unique numeric identifier. This methods returns the <i>next</i> free identifier of the current
	 * feature model and is a <b>state-full</b> operation, such that invoking the method twice will result in two other numeric identifiers. <br> <br> The
	 * default implementations provides this by the following code snippet: <code> private static long NEXT_ID = 0;
	 *
	 * protected static final synchronized long getNextId() { return NEXT_ID++; } </code> <b>Notes to thread-safe execution</b>: The management of receiving the
	 * next free identifier must be thread-safe.
	 *
	 * @see #getId()
	 *
	 * @since 3.0
	 *
	 * @return the next free system-wide unique identifier for feature models
	 */
	public final synchronized long getNextElementId() {
		return nextElementId++;
	}

	protected final String factoryID;

	protected final List<Constraint> constraints = new ArrayList<>();

	/**
	 * A list containing the feature names in their specified order will be initialized in XmlFeatureModelReader.
	 */
	protected final List<String> featureOrderList;
	protected boolean featureOrderUserDefined;
	/**
	 * A {@link Map} containing all features.
	 */
	protected final Map<String, Feature> featureTable = Collections.synchronizedMap(new LinkedHashMap<>());
	protected final Map<Long, FeatureModelElement> elements = Collections.synchronizedMap(new LinkedHashMap<>());

	protected IEventManager eventManager = new DefaultEventManager();

	protected final IFeatureModelProperty property;

	//protected final RenamingsManager renamingsManager;

	protected final FeatureModelStructure structure;

	protected Path sourceFile;

	public FeatureModel(String factoryID) {
		this.factoryID = factoryID;

		id = getNextId();
		featureOrderList = new LinkedList<>();
		featureOrderUserDefined = false;

		property = createProperty();
		structure = createStructure();

		//renamingsManager = new RenamingsManager(this);
	}

	protected FeatureModel(FeatureModel oldFeatureModel, Feature newRoot) {
		factoryID = oldFeatureModel.factoryID;
		id = oldFeatureModel.id;
		nextElementId = oldFeatureModel.nextElementId;
		featureOrderList = new LinkedList<>(oldFeatureModel.featureOrderList);
		featureOrderUserDefined = oldFeatureModel.featureOrderUserDefined;

		property = oldFeatureModel.getProperty().clone(this);
		structure = createStructure();

		//renamingsManager = oldFeatureModel.renamingsManager.clone(this);

		sourceFile = oldFeatureModel.sourceFile;

		if (newRoot == null) {
			final FeatureTree root = oldFeatureModel.getStructure().getRoot();
			if (root != null) {
				structure.setRoot(root.cloneSubtree(this));// structure.getRoot().cloneSubtree(this));
				for (final Constraint constraint : oldFeatureModel.constraints) {
					constraints.add(constraint.clone(this));
				}
			}
		} else {
			structure.setRoot(newRoot.getStructure().cloneSubtree(this));
			for (final Constraint constraint : oldFeatureModel.constraints) {
				if (featureTable.keySet().containsAll(constraint.getContainedFeatures().stream().map(Feature::getName).collect(Collectors.toList()))) {
					constraints.add(constraint.clone(this));
				}
			}
		}
	}

	protected IFeatureModelProperty createProperty() {
		return new FeatureModelProperty(this);
	}

	protected FeatureModelStructure createStructure() {
		return new FeatureModelStructure(this);
	}

	/**
	 * A constraint is an additional restriction on features in the feature model.
	 *
	 * This methods adds the constraint <code>constraint</code> to the <i>end</i> of the existing collection. Please note that <ul> <li>the specification do not
	 * require a check if <code>constraint</code> is <i>null</i>. However, for regular use, <code>constraint</code> is assumed to be <i>non-null</i></li>
	 * <li>the specification do not require a check of duplicates. In FeatureIDE's default implementation, the collection is managed using a <code>List</code>.
	 * For regular use case, this collection is assumed to be duplicate-free. Therefore, duplicates should not be added.</li> </ul>
	 *
	 * To add a constraint at a specific position, use {@link #addConstraint(Constraint, int)}
	 *
	 * @param constraint The constraint to be added at the end of the existing collection
	 *
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 */
	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
		elements.put(constraint.getInternalId(), constraint);
	}

	/**
	 * A constraint is an additional restriction on features in the feature model.
	 *
	 * This methods adds the constraint <code>constraint</code> at the given <i>index</i> of the existing collection. Please note that <ul> <li>the
	 * specification do not require a check if <code>constraint</code> is <i>null</i>. However, for regular use, <code>constraint</code> is assumed to be
	 * <i>non-null</i></li> <li>the specification do not require a check of duplicates. In FeatureIDE's default implementation, the collection is managed using
	 * a <code>List</code>. For regular use case, this collection is assumed to be duplicate-free. Therefore, duplicates should not be added.</li> </ul>
	 *
	 * To add a constraint at a specific position, use {@link #addConstraint(Constraint, int)}
	 *
	 * @param constraint The constraint to be added at position <i>index</i> of the existing collection
	 * @param index The position. It is assumed, that the index is valid. Otherwise a exception have to be thrown by the implementation.
	 *
	 * @see #addConstraint(Constraint)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 */
	public void addConstraint(Constraint constraint, int index) {
		constraints.add(index, constraint);
		elements.put(constraint.getInternalId(), constraint);
	}

	/**
	 * Add a new feature <code>feature</code> to this feature model. If the feature model not contains a feature with the name {@link Feature#getName()} of
	 * <code>feature</code>, the <code>feature</code> will be added and the method returns <b>true</b>. Otherwise, the feature is not added and the method
	 * returns <b>false</b>. Classes implementing <code>IFeatureModel</code> must provide consistency with the underlying <i>feature table</i> which is
	 * accessible by {@link #getFeatureTable()}.
	 *
	 * @param feature the feature to be added. <code>feature</code> is assumed to be <i>non-null</i>
	 * @return <b>true</b> if the feature was added, otherwise <b>false</b>.
	 *
	 * @see #deleteFeature(Feature)
	 * @see #getFeature(CharSequence)
	 * @see #getFeatures()
	 * @see #getNumberOfFeatures()
	 * @see #reset()
	 *
	 * @since 3.0
	 */
	public boolean addFeature(Feature feature) {
		final CharSequence name = feature.getName();
		if (featureTable.containsKey(name)) {
			return false;
		}
		featureTable.put(name.toString(), feature);
		elements.put(feature.getInternalId(), feature);
		return true;
	}

	public List<IEventListener> getListenerList() {
		return eventManager.getListeners();
	}

	/**
	 * Clones this feature model <code>f</code>, such that a new instance <code>f'</code> is created. The cloned feature model <code>f'</code> must satisfy the
	 * following properties to contain the same information as <code>f</code>: <ul> <li>the identifiers of <code>f</code> and <code>f'</code> must be
	 * identical</li> <li>the feature order list of <code>f</code> and <code>f'</code> must be equal, but the references must be different</li> <li>the user
	 * defined feature order flag of <code>f</code> and <code>f'</code> must be identical</li> <li>the feature models properties must be equal but with
	 * different references in <code>f</code> and <code>f'</code></li> <li>the feature models constraints must be equal but with different references in
	 * <code>f</code> and <code>f'</code></li> <li>the cloned feature model <code>f'</code> must contain the structure behind <code>newRoot</code> but with
	 * different references</li> <li>the cloned feature model <code>f'</code>'s root feature must be <code>newRoot</code></li> </ul> Additionally, the following
	 * must hold <code>f != f'</code> and <code>f.equals(f')</code>.
	 *
	 * @param newRoot the new root feature including the entire structure of <code>newRoot</code> for the cloned model
	 * @return A new equal instance of this feature model with different references and <code>newRoot</code> as root feature
	 *
	 * @since 3.0
	 */
	public org.spldev.featuremodel.FeatureModel clone(Feature newRoot) {
		return new FeatureModel(this, newRoot);
	}

	/**
	 * Resets this feature model to the default values. The parameter <code>projectName</code> will be used as the new root features name if there are no
	 * features in this model (the <i>feature table</i> is empty). Independent of this, a new feature called <code>Base</code> will be added as child of the
	 * feature models root feature, and the feature models root feature will be set as <i>abstract feature</i>.
	 *
	 * @param projectName the name for the root feature, if this feature model does not contain any features. Otherwise this parameter will be ignored. If
	 *        <code>projectName</code> is an empty string, the string <code>"Root"</code> will be used for the potential new root feature. The parameter
	 *        <code>projectName</code> is assumed to be <i>non-null</i>
	 *
	 * @see #reset()
	 *
	 * @since 3.0
	 */
	public void createDefaultValues(CharSequence projectName) {
		String rootName = getValidJavaIdentifier(projectName);
		if (rootName.isEmpty()) {
			rootName = "Root";
		}
		if (featureTable.isEmpty()) {
			final Feature root = new Feature(this, rootName);
			structure.setRoot(root.getStructure());
			addFeature(root);
		}
		final Feature feature = new Feature(this, "Base");
		addFeature(feature);

		structure.getRoot().addChild(feature.getStructure());
		structure.getRoot().setAbstract(true);
	}

	/**
	 * Removes <code>feature</code> from this model. <code>feature</code> can not be removed, if it is the feature models <i>root</i> feature or if it is not
	 * contained in this model. In both cases, the method returns <b>false</b>. Otherwise the method returns <b>true</b>. <br> <br> Implementations of this
	 * method must ensure, that after removing <code>feature</code>, the feature's <i>parent feature</i> is changed to an <i>and</i> ( <i>or</i>,
	 * <i>alternative</i>) group if <code>feature</code> was an <i>and</i> (<i>or</i>, <i>alternative</i>) group. Additionally, removing <code>feature</code>
	 * has to add the children of <code>feature</code> as children to the <i>parent feature</i>. <br> <br> Removing a feature also removes this feature from the
	 * <i>feature table</i> and the <i>feature order list</i>. Both must be consistent with {@link #getFeatureOrderList()} and {@link #getFeatureOrderList()}
	 * <br> <br> <b>Note</b>If the structure should not be changed, use {@link #deleteFeatureFromTable(Feature)}
	 *
	 * @param feature the feature that should be removed. It is assumed to be <i>non-null</i>
	 * @return <b>false</b> if <code>feature</code> is the models <i>root</i> feature, or if <code>feature</code> is not contained in this model. Otherwise
	 *         <b>true</b>.
	 *
	 * @see #addFeature(Feature)
	 * @see #getFeature(CharSequence)
	 * @see #getFeatures()
	 * @see #getNumberOfFeatures()
	 * @see #reset()
	 *
	 * @since 3.0
	 */
	public boolean deleteFeature(Feature feature) {
		// the root can not be deleted
		if (feature.equals(structure.getRoot().getFeature())) {
			return false;
		}

		// check if it exists
		final CharSequence name = feature.getName();
		if (!featureTable.containsKey(name)) {
			return false;
		}

		// use the group type of the feature to delete
		final FeatureTree parent = feature.getStructure().getParent().orElseThrow();

		if (parent.getNumberOfChildren() == 1) {
			if (feature.getStructure().isAnd()) {
				parent.setAnd();
			} else if (feature.getStructure().isAlternative()) {
				parent.setAlternative();
			} else {
				parent.setOr();
			}
		}

		// add children to parent
		final int index = parent.getChildIndex(feature.getStructure());
		while (feature.getStructure().hasChildren()) {
			parent.addChild(index, feature.getStructure().removeChild(feature.getStructure().getNumberOfChildren() - 1));
		}

		// delete feature
		parent.removeChild(feature.getStructure());
		featureTable.remove(name);
		elements.remove(feature.getInternalId());
		featureOrderList.remove(name);
		return true;
	}

	/**
	 * Removes the feature <code>feature</code> from the <i>feature table</i> by <code>feature</code>'s name with {@link Feature#getName()}. If the <i>feature
	 * table</i> does not contain a feature with such a name, there will be no changes. <br> <br> This method only affects the collection of features stored in
	 * the feature model, but do not change the <i>structure</i> neither of <code>feature</code> nor it's <i>parent</i> or <i>children</i>. <br> <br>
	 * <b>Note</b> There is no equality check over the identifiers between the feature to be deleted and the feature contained in the collection, expect for
	 * equality in their names. To avoid confusion, this check should be done before calling this method. <br> <b>Note</b> If the structure should be changed,
	 * use {@link #deleteFeature(Feature)}
	 *
	 * @see #setFeatureTable(Hashtable)
	 * @see #getFeatureTable()
	 *
	 * @param feature the feature (the feature's name) which should be deleted from the <i>feature table</i>
	 *
	 * @since 3.0
	 */
	public void deleteFeatureFromTable(Feature feature) {
		featureTable.remove(feature.getName());
		elements.remove(feature.getInternalId());
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

	protected void fireEvent(final FeatureIDEEvent.EventType action) {
		fireEvent(new FeatureIDEEvent(this, action, Boolean.FALSE, Boolean.TRUE));
	}

	/**
	 * @return Returns the number of constraints contained in this feature model.
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 */
	public int getConstraintCount() {
		return constraints.size();
	}

	/**
	 * Returns the index of the first occurrence of <code>constraint</code> in the collection of constraints, or <b>-1</b> if <code>constraint</code> is not
	 * contained. <br> <br> <b>Note</b>:
	 *
	 * @param constraint the element to be removed. It is assumed that this parameter is <i>non-null</i>
	 * @throws NullPointerException - if <code>constraint</code> is null (optional)
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 *
	 * @return the index of the first occurrence of <code>constraint</code> in the collection of constraints, or <b>-1</b> otherwise.
	 */
	public int getConstraintIndex(Constraint constraint) {
		return constraints.indexOf(constraint);
	}

	/**
	 * Returns the list of constraints stored in this feature model. <br> <br> <b>Note</b>: The returned list should be <b>unmodifiable</b> to avoid external
	 * access to internal data
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 *
	 * @return All constraints stored in this feature model.
	 */
	public List<Constraint> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	/**
	 * Returns the feature with the given <code>name</code> stored in this feature model, or <code>null</code> if no features can be found. The given
	 * <code>name</code> is compared to the names of the contained features in a <i>case-sensitive</i> manner. Therefore <code>"FeatureA"</code> is unequal to
	 * <code>featureA</code>.
	 *
	 * @param name the name (case sensitive) of the feature which should be return. This parameter is assumed to be non-null.
	 *
	 * @see #addFeature(Feature)
	 * @see #deleteFeature(Feature)
	 * @see #getFeatures()
	 * @see #getNumberOfFeatures()
	 * @see #reset()
	 *
	 * @since 3.0
	 *
	 * @return the associated feature, if there is a match to <code>name</code>, or <b>null</b> otherwise.
	 */
	public Feature getFeature(CharSequence name) {
		return featureTable.get(name);
	}

	/**
	 * Returns the ordered collection of feature names according to the given feature order. If an order is given, the method returns the corresponding list of
	 * feature names according to their order. If no order is set, the method returns the names of features according to a pre-order traversation of the root
	 * feature's structure. In both cases, the resulting collection is <b>unmodifiable</b>.
	 *
	 * @see #setFeatureOrderList(List)
	 * @see #setFeatureOrderListItem(int, String)
	 * @see #setFeatureOrderUserDefined(boolean)
	 *
	 * @since 3.0
	 *
	 * @return an ordered list of feature names, either as a given order or in pre-order by traversing the root-feature.
	 */
	public List<String> getFeatureOrderList() {
		if (featureOrderList.isEmpty()) {
			return Trees.preOrderStream(getStructure().getRoot())
					.filter(FeatureTree::isConcrete)
					.map(FeatureTree::getFeature)
					.map(Feature::getName)
					.collect(Collectors.toList());
		}
		return Collections.unmodifiableList(featureOrderList);
	}


	/**
	 * Returns the a read-only iterable collection of features stored in this feature model. This method is intend to provide the iteration-concept directly.
	 * <br> <br> <b>Example</b> <code> for (IFeature feature : featureModel.getFeatures()) { // ... } </code> If a list interface is required rather than the
	 * iterable counterpart, the utility class {@link Functional} provides a set of useful methods. To convert the iterator directly into a list, use
	 * {@link Functional#toList(Iterable)}. By using methods from the {@link Functional} utility class the advantages of a functional-like programming style can
	 * be directly used. For instance, to convert the collection of features inside a feature model into a set of feature names, the following code snippet can
	 * be used: <br><br><code> import static de.ovgu.featureide.fm.core.functional.Functional.*;
	 *
	 * <br>Set&lt;String&gt; featureNames = new HashSet&lt;&gt;(toList(mapToString(fm.getFeatures()))) </code> <br>If modification is required, use the related
	 * constructor for collection implementations, e.g., <br> <code>List&lt;IFeature&gt; list = new
	 * LinkedList&lt;IFeature&gt;(Functional.toList(fm.getFeatures()));</code> <br> <b>Note</b>: Many operations of features in feature models runs over
	 * iteration. This method returns an iterator rather than a collection for <i>lazy evaluation</i> purposes. <br>
	 *
	 * @see Functional FeatureIDE functional helper class
	 * @see #addFeature(Feature)
	 * @see #deleteFeature(Feature)
	 * @see #getFeature(CharSequence)
	 * @see #getNumberOfFeatures()
	 * @see #reset()
	 *
	 * @since 3.0
	 *
	 * @return features of the feature model.
	 */
	public Collection<Feature> getFeatures() {
		return Collections.unmodifiableCollection(featureTable.values());
	}


	/**
	 * Returns the a read-only iterable collection of features stored in this feature model, which are not collapsed. This method is intend to provide the
	 * iteration-concept directly. <br> <br> <b>Example</b> <code> for (IFeature feature : featureModel.getVisibleFeatures()) { // ... } </code> If a list
	 * interface is required rather than the iterable counterpart, the utility class {@link Functional} provides a set of useful methods. To convert the
	 * iterator directly into a list, use {@link Functional#toList(Iterable)}. By using methods from the {@link Functional} utility class the advantages of a
	 * functional-like programming style can be directly used. For instance, to convert the collection of features inside a feature model into a set of feature
	 * names, the following code snippet can be used: <code> import static de.ovgu.featureide.fm.core.functional.Functional.*;
	 *
	 * Set&lt;String&gt; featureNames = new HashSet&lt;&gt;(toList(mapToString(fm.getVisibleFeatures()))) </code> If modification is required, use the related
	 * constructor for collection implementations, e.g., <br> <code>List&lt;IFeature&gt; list = new
	 * LinkedList&lt;IFeature&gt;(Functional.toList(fm.getVisibleFeatures()));</code> <br> <b>Note</b>: Many operations of features in feature models runs over
	 * iteration. This method returns an iterator rather than a collection for <i>lazy evaluation</i> purposes. <br>
	 *
	 *
	 * @see Functional FeatureIDE functional helper class
	 * @see #addFeature(Feature)
	 * @see #deleteFeature(Feature)
	 * @see #getFeature(CharSequence)
	 * @see #getNumberOfFeatures()
	 * @see #reset()
	 *
	 * @since 3.3
	 *
	 * @return A iterable of IFeatures, which are not collapsed
	 */
	public Collection<Feature> getVisibleFeatures() {
		final Collection<Feature> features = new ArrayList<>();
		for (final Feature f : getFeatures()) {
			features.add(f);
		}
		return features;
	}

	/**
	 * Returns the number of features stored in this feature model. This call must be constistent with {@link FeatureModel#getFeatureTable()} size.
	 *
	 * @see #addFeature(Feature)
	 * @see #deleteFeature(Feature)
	 * @see #getFeature(CharSequence)
	 * @see #getFeatures()
	 * @see #reset()
	 *
	 * @since 3.0
	 *
	 * @return number of feature stored in this model
	 */
	public int getNumberOfFeatures() {
		return featureTable.size();
	}

	/**
	 * Returns the model properties attached to this feature model. These properties contain at least <ul> <li>Annotations</li> <li>Comments</li> <li>The
	 * feature order specification</li> </ul> The properties returned by this model is implementation specific and might contain additional properties (see
	 * {@link IFeatureModelProperty}).
	 *
	 * @since 3.0
	 *
	 * @return feature model properties
	 */
	public IFeatureModelProperty getProperty() {
		return property;
	}

	/**
	 * Returns the feature models {@link FeatureModelStructure} instance. In this features can be received in preorder, and further structural properties can
	 * be get. For instance, the structure holds information if alternative groups are contained, or the number of or-groups in total. For more information, see
	 * {@link FeatureModelStructure}.
	 *
	 * @since 3.0
	 *
	 * @return This feature model's structure
	 */
	public FeatureModelStructure getStructure() {
		return structure;
	}

	/**
	 * Removes all invalid java identifiers form a given string.
	 */
	protected String getValidJavaIdentifier(CharSequence s) {
		final StringBuilder stringBuilder = new StringBuilder();
		int i = 0;
		for (; i < s.length(); i++) {
			if (Character.isJavaIdentifierStart(s.charAt(i))) {
				stringBuilder.append(s.charAt(i));
				i++;
				break;
			}
		}
		for (; i < s.length(); i++) {
			if (Character.isJavaIdentifierPart(s.charAt(i))) {
				stringBuilder.append(s.charAt(i));
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Fires the the event {@link org.spldev.featuremodel.event.FeatureIDEEvent.EventType#MODEL_DATA_CHANGED} to listeners.
	 *
	 * @since 3.0
	 */
		public void handleModelDataChanged() {
		fireEvent(FeatureIDEEvent.EventType.MODEL_DATA_CHANGED);
	}

	/**
	 * @since 3.0
	 *
	 * @see #setFeatureOrderUserDefined(boolean)
	 *
	 * @return Returns if a user defined order for features in this model is used.
	 */
	public boolean isFeatureOrderUserDefined() {
		return featureOrderUserDefined;
	}

	/**
	 * Removes the first occurrence of <code>constraint</code> from the collection of constraints in this model, if it is present. Otherwise there is no effect
	 * to this model.
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 *
	 * @param constraint The constraint to be removed
	 */
	public void removeConstraint(Constraint constraint) {
		constraints.remove(constraint);
		elements.remove(constraint.getInternalId());
	}

	/**
	 * Removes the constraint at the specified position <code>index</code> in this collection of constraints in this model. When a constraint was removed, the
	 * remaining constraints to the right are shifted one position to the left.
	 *
	 * @param index position of the constraint to be removed
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @throws IndexOutOfBoundsException If the index is out of range
	 * @since 3.0
	 */
	public void removeConstraint(int index) {
		final Constraint constraint = constraints.remove(index);
		elements.remove(constraint.getInternalId());
	}

	/**
	 * Replaces the constraint <code>constraint</code> at the specified position <code>index</code> in the collection of constraints of this feature model.
	 *
	 * @param constraint constraint which should be stored at <code>index</code>
	 * @param index position for replacement
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #setConstraints(Iterable)
	 *
	 * @throws NullPointerException if <code>constraint</code> is <b>null</b>
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *
	 * @since 3.0
	 *
	 */
	public void replaceConstraint(Constraint constraint, int index) {
		if (constraint == null) {
			throw new NullPointerException();
		}
		elements.remove(constraints.get(index).getInternalId());
		constraints.set(index, constraint);
		elements.put(constraint.getInternalId(), constraint);
	}

	/**
	 * Set the feature models structure root element to <b>null</b> and clears the collections of features and constraints. Moreover, the feature order list is
	 * cleared and all properties. The next unique element identifier is also reseted to <b>0</b>, such that {@link FeatureModel#getNextElementId()} will
	 * return <b>0</b>.
	 *
	 * @see #deleteFeature(Feature)
	 * @see #removeConstraint(int)
	 * @see #removeConstraint(Constraint)
	 * @see #createDefaultValues(CharSequence)
	 *
	 * @since 3.0
	 */
	public void reset() {
		structure.setRoot(null);

		featureTable.clear();
		//renamingsManager.clear();
		constraints.clear();
		featureOrderList.clear();
		elements.clear();

		property.reset();
		nextElementId = 0;
	}

	/**
	 * Sets the collections of constraints to the ones yielded by <code>constraints</code>. Existing constraint in the collection will be removed before this
	 * operation.
	 *
	 * @param constraints Source of constraints which should be copied into this feature model
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraint(int, Constraint)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 */
	public void setConstraints(Iterable<Constraint> constraints) {
		this.constraints.clear();
		for (final Constraint constraint : constraints) {
			addConstraint(constraint);
		}
	}

	/**
	 * Sets the list of feature names for ordering purposed to the content provided by <code>featureOrderList</code>. Existing ordering will be removed before
	 * this operation is executed. There is no check if the feature names provided by <code>featureOrderList</code> actually reflects names of features stored
	 * in this model. <br> <br> The order of strings provided in <code>featureOrderList</code> provide the order of feature names.
	 *
	 * @see #getFeatureOrderList()
	 * @see #setFeatureOrderListItem(int, String)
	 * @see #setFeatureOrderUserDefined(boolean)
	 *
	 * @param featureOrderList the orderd list of feature names which provides the feature order. This parameter is assumed to be <i>non-null</i>
	 *
	 * @since 3.0
	 */
	public void setFeatureOrderList(List<String> featureOrderList) {
		List<String> basicSet = Trees.preOrderStream(getStructure().getRoot())
				.filter(FeatureTree::isConcrete)
				.map(FeatureTree::getFeature)
				.map(Feature::getName)
				.collect(Collectors.toList());
		// TODO optimize performance
		basicSet.removeAll(featureOrderList);
		this.featureOrderList.clear();
		this.featureOrderList.addAll(featureOrderList);
		this.featureOrderList.addAll(basicSet);
	}

	/**
	 * Sets a flag that specificities if the feature order in this feature model user defined or not.
	 *
	 * @see #getFeatureOrderList()
	 * @see #setFeatureOrderList(List)
	 * @see #setFeatureOrderListItem(int, String)
	 *
	 * @param featureOrderUserDefined flag to indicate user defined ordering
	 *
	 * @since 3.0
	 */
	public void setFeatureOrderUserDefined(boolean featureOrderUserDefined) {
		this.featureOrderUserDefined = featureOrderUserDefined;
	}

	/**
	 * Overwrites the contents of the <i>feature table</i> with the given <code>featureTable</code>. The existing feature table will be cleared and each element
	 * in <code>featureTable</code> will be inserted in the feature model's underlying feature table. There is no check, if the the mapping of features names to
	 * features in <code>featureTable</code> is consistent. Moreover, there is no check if the feature names in <code>featureTable</code> corresponds to the
	 * feature names in this feature model. Therefore, overwriting the contents of the feature table by this function might lead to unexpected behavior, when
	 * not used correctly.
	 *
	 * @see #deleteFeatureFromTable(Feature)
	 * @see #getFeatureTable()
	 *
	 * @param featureTable New feature table for this feature model. This parameter is assumed to be <i>non-null</i>
	 *
	 * @since 3.0
	 */
	public void setFeatureTable(Hashtable<String, Feature> featureTable) {
		this.featureTable.clear();
		elements.clear();
		this.featureTable.putAll(featureTable);
		for (final Feature feature : featureTable.values()) {
			elements.put(feature.getInternalId(), feature);
		}
		for (final Constraint constraint : constraints) {
			elements.put(constraint.getInternalId(), constraint);
		}
	}

	/**
	 * @see #setFeatureTable(Hashtable)
	 * @see #deleteFeatureFromTable(Feature)
	 *
	 * @return Returns this feature model's underlying <i>feature table</i> as an <b>unmodifiable map</b>.
	 *
	 * @since 3.0
	 */
	public Map<String, Feature> getFeatureTable() {
		return Collections.unmodifiableMap(featureTable);
	}

	/**
	 * Replaces the feature order item at the specified position <code>i</code> in this feature model's feature order list with the specified element
	 * <code>newName</code>.
	 *
	 * @param i index of the element to replace
	 * @param newName new name to be stored at the specified position
	 *
	 * @see #getFeatureOrderList()
	 * @see #setFeatureOrderList(List)
	 * @see #setFeatureOrderUserDefined(boolean)
	 *
	 * @since 3.0
	 *
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setFeatureOrderListItem(int i, String newName) {
		if (!featureOrderList.isEmpty()) {
			featureOrderList.set(i, newName);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FeatureModel(");
		if (getStructure().getRoot() != null) {
			sb.append("Structure=[");
			sb.append(Trees.print(getStructure().getRoot()));
			sb.append("], Constraints=[");
			print(getConstraints(), sb);
			sb.append("], ");
		} else {
			sb.append("Feature model without root feature.");
		}
		final StringBuilder features = new StringBuilder();
		final String[] feat = featureTable.keySet().toArray(new String[featureTable.keySet().size()]);
		for (int i = 0; i < feat.length; i++) {
			features.append(feat[i]);
			if ((i + 1) < feat.length) {
				features.append(", ");
			}
		}
		sb.append("Features=[" + (features.length() > 0 ? features.toString() : ""));
		sb.append("])");
		return sb.toString();
	}

	protected void print(List<Constraint> constraints, StringBuilder sb) {
		for (int i = 0; i < constraints.size(); i++) {
			sb.append("[");
			sb.append(new NodeWriter().write(constraints.get(i).getNode()));
			sb.append("]");
			if ((i + 1) < constraints.size()) {
				sb.append(", ");
			}
		}
	}

	/**
	 * Set the feature models source file to <code>file</code>. By definition, the feature model's unique identifier is bidirectional mapped to the source
	 * files. Therefore, two feature model's based on the same file must have to same unique identifier. The feature model's identifier will not be changed, if
	 * <code>file</code> is <b>null</b>. <br><br> The default implementation provides this mechanism by using {@link ModelFileIdMap}, such that: <code>
	 * this.sourceFile = file; if (file != null) { id = ModelFileIdMap.getModelId(this, file); } </code> <b>Note</b>: The specification does not require to
	 * reload the content of this feature model, when the source file is changes. Hence, using this method only will affect the return value of
	 * {@link #getSourceFile()} and perhaps {@link #getId()}. However, it is not intended to notify listeners about this change.
	 *
	 * @see #getSourceFile()
	 * @see ModelFileIdMap#getModelId(FeatureModel, Path)
	 *
	 * @since 3.0
	 *
	 * @param file the source file of this model (might be <b>null</b>.
	 */
	public void setSourceFile(Path file) {
		sourceFile = file;
		if (file != null) {
			id = ModelFileIdMap.getModelId(this, file);
		}
	}

	/**
	 * @see #setSourceFile(Path)
	 *
	 * @since 3.0
	 *
	 * @return Returns the feature models current source file, or <b>null</b> if no source file is specified.
	 */
	public Path getSourceFile() {
		return sourceFile;
	}

	/**
	 * Feature models are identified with their system-wide unique numeric identifier. This methods returns the identifier of the current feature model. Custom
	 * implementations might manage the feature model's identifier similar to the default implementation: <code> private static long NEXT_ID = 0;
	 *
	 * protected static final synchronized long getNextId() { return NEXT_ID++; } </code> The identifier has to be used for comparisons using
	 * {@link Object#equals(Object)}.
	 *
	 * @return unique identifier
	 *
	 * @see #getNextElementId()
	 *
	 * @since 3.0
	 */
	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return (int) (37 * id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final FeatureModel other = (FeatureModel) obj;
		return id == other.id;
	}

	/**
	 * Overwrites the constraint stored in this feature model at position <code>index</code> with the constraint provided by the parameter
	 * <code>constraint</code>.
	 *
	 * @param index index of the constraint to replace
	 * @param constraint constraint to be stored at the specified position
	 *
	 * @see #addConstraint(Constraint)
	 * @see #addConstraint(Constraint, int)
	 * @see #getConstraintCount()
	 * @see #getConstraintIndex(Constraint)
	 * @see #getConstraints()
	 * @see #removeConstraint(Constraint)
	 * @see #removeConstraint(int)
	 * @see #setConstraints(Iterable)
	 * @see #replaceConstraint(Constraint, int)
	 *
	 * @since 3.0
	 *
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setConstraint(int index, Constraint constraint) {
		constraints.set(index, constraint);
	}

	/**
	 * Clones this feature model <code>f</code> to a new instance of feature model <code>f'</code>, such that <code>f != f'</code> and <code>f.equals(f')</code>
	 * holds. More in detail: <ul> <li>Both feature model's unique identifiers are equal</li> <li>Both feature order lists are equal but their references aren't
	 * identical</li> <li>Both feature order lists user defined order flag is equal</li> <li>Both feature order lists property and structure are equal, but
	 * their references aren't identical</li> <li>Both feature model's source files are equal but their references aren't identical</li> <li>Both feature
	 * model's feature structure (including their constraints) are equal but their references aren't identical</li> <li>The feature model <code>f'</code>'
	 * feature model analyzer instance is a <i>new</i> instance</li> </ul>
	 *
	 * @since 3.0
	 *
	 * @see #getId()
	 * @see #getFeatureOrderList()
	 * @see #isFeatureOrderUserDefined()
	 * @see #getStructure()
	 * @see #getProperty()
	 * @see #getSourceFile()
	 * @see #getStructure()
	 * @see #getConstraints()
	 *
	 * @return cloned instance of this model, such that the new instance is equal to this feature model but their references aren't identical
	 */
	public FeatureModel clone() {
		return new FeatureModel(this, null);
	}

	/**
	 * A feature model is created via a feature model {@link IFeatureModelFactory factory}. This methods returns the identifier of the factory used to create
	 * this feature model. The factory can be used to create more feature models, features, or constraint from the same type as this feature model.
	 *
	 * @return the feature model factory ID.
	 *
	 * @see FMFactoryManager#getFactory(String)
	 *
	 * @since 3.1
	 */
	public String getFactoryID() {
		return factoryID;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public FeatureModelElement getElement(long id) {
		return elements.get(id);
	}

	/**
	 * Sets the next element ID to the correct value for this feature model to avoid duplicate IDs.
	 */
	public void updateNextElementId() {
		long max = 0;
		final List<FeatureModelElement> elements = new ArrayList<>();
		elements.addAll(getFeatures());
		elements.addAll(getConstraints());

		for (final FeatureModelElement element : elements) {
			if (element.getInternalId() > max) {
				max = element.getInternalId();
			}
		}
		nextElementId = max + 1;
	}

	public void setNextElementId(long nextElementId) {
		this.nextElementId = nextElementId;
	}

	/**
	 * @since 3.0
	 *
	 * @return Returns an instance of {@link RenamingsManager} which is bound to this feature model.
	 */
	//RenamingsManager getRenamingsManager();
}
