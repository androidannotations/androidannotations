package com.googlecode.androidannotations.model;

public class MetaView {
	
	private final String fieldName;

	private final String typeQualifiedName;

	private final String viewQualifiedId;

	public MetaView(String fieldName, String typeQualifiedName, String viewQualifiedId) {
		this.fieldName = fieldName;
		this.typeQualifiedName = typeQualifiedName;
		this.viewQualifiedId = viewQualifiedId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getTypeQualifiedName() {
		return typeQualifiedName;
	}

	public String getViewQualifiedId() {
		return viewQualifiedId;
	}
	
	

}
