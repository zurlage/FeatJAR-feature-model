package de.featjar.feature.model;

import de.featjar.base.data.IAttributable;
import de.featjar.base.data.identifier.IIdentifiable;

public interface IFeatureModelElement extends IIdentifiable, IAttributable {
    IFeatureModel getFeatureModel();
}
