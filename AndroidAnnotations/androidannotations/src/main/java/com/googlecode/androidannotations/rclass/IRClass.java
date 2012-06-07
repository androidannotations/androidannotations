/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.rclass;

public interface IRClass {

	public enum Res {
		LAYOUT, ID, STRING, ARRAY, COLOR, ANIM, BOOL, DIMEN, DRAWABLE, INTEGER, MOVIE, MENU, RAW;
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
