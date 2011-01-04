package com.googlecode.androidannotations.validation;

public class IsValid {
	
	boolean valid = true;
	
	public void invalidate() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}
}
