package com.googlecode.androidannotations.rclass;

import com.googlecode.androidannotations.rclass.RClass.Res;

public interface IRClass {

	IRInnerClass get(Res res);

	final IRClass EMPTY_R_CLASS = new IRClass() {
		@Override
		public IRInnerClass get(Res res) {
			return IRInnerClass.EMPTY_R_INNER_CLASS;
		}
	};

}