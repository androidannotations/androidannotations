package org.androidannotations.test15.parcelable;

import org.androidannotations.annotations.NonParcelable;
import org.androidannotations.annotations.Parcelable;

@Parcelable
public class Bean1 {
	String stringField;
	int intField; 
	String[] stringArrayField;
	byte[] byteArrayField;
	protected Integer protectedIntegerField;
	boolean booleanField;
	
	@NonParcelable
	String nonParcelable;
	private String privateStringField;
	transient int transientIntField;
	
	public String getPrivateStringField() {
		return privateStringField;
	}
	public void setPrivateStringField(String privateStringField) {
		this.privateStringField = privateStringField;
	}
	
	
}
