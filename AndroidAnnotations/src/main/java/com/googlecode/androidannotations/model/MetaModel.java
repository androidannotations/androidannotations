package com.googlecode.androidannotations.model;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

public class MetaModel {

	private final Map<Element, MetaActivity> metaActivities = new HashMap<Element, MetaActivity>();

	public Map<Element, MetaActivity> getMetaActivities() {
		return metaActivities;
	}

}
