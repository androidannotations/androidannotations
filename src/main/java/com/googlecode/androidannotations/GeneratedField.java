package com.googlecode.androidannotations;


public class GeneratedField {
	
	private static final String FIELD_FORMAT = "        %s = (%s) findViewById(%s);\n";

	private String name;

	private String typeQualifiedName;

	private String viewQualifiedId;
	
	public String writeField() {
		return String.format(FIELD_FORMAT, name, typeQualifiedName, viewQualifiedId);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypeQualifiedName(String typeQualifiedName) {
		this.typeQualifiedName = typeQualifiedName;
	}

	public void setViewQualifiedId(String viewQualifiedId) {
		this.viewQualifiedId = viewQualifiedId;
	}

}
