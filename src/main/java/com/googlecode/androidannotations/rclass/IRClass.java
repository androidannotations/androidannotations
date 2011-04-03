package com.googlecode.androidannotations.rclass;


public interface IRClass {

	public enum Res {
    	LAYOUT, ID, STRING, ARRAY, COLOR, ANIM, BOOL, DIMEN, DRAWABLE, INTEGER, MOVIE;
    	public String rName() {
    		return toString().toLowerCase();
    	}
    }

    IRInnerClass get(Res res);

	final IRClass EMPTY_R_CLASS = new IRClass() {
		@Override
		public IRInnerClass get(Res res) {
			return IRInnerClass.EMPTY_R_INNER_CLASS;
		}
	};

}