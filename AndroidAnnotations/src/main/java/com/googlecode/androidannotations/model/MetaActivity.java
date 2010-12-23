package com.googlecode.androidannotations.model;

import java.util.ArrayList;
import java.util.List;

public class MetaActivity {

	private static final String CLASS_SUFFIX = "_";

	private final String packageName;

	private final String superClassName;

	private final String layoutQualifiedName;

	private final List<MetaView> metaViews = new ArrayList<MetaView>();

	public MetaActivity(String packageName, String superClassName, String layoutQualifiedName) {
		this.packageName = packageName;
		this.superClassName = superClassName;
		this.layoutQualifiedName = layoutQualifiedName;
	}

	public String getClassQualifiedName() {
		return packageName + "." + getClassSimpleName();
	}

	public String getClassSimpleName() {
		return superClassName + CLASS_SUFFIX;
	}

	public List<MetaView> getMetaViews() {
		return metaViews;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public String getLayoutQualifiedName() {
		return layoutQualifiedName;
	}

}
