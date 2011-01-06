/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.rclass;

public class CoumpoundInnerClass implements IRInnerClass {
	
	private final IRInnerClass rInnerClass;
	private final IRInnerClass androidRInnerClass;
	
	public CoumpoundInnerClass(IRInnerClass rInnerClass, IRInnerClass androidRInnerClass) {
		this.rInnerClass = rInnerClass;
		this.androidRInnerClass = androidRInnerClass;
	}

	@Override
	public boolean containsIdValue(Integer idValue) {
		return rInnerClass.containsIdValue(idValue) ||  androidRInnerClass.containsIdValue(idValue) ;
	}

	@Override
	public String getIdQualifiedName(Integer idValue) {
		String idQualifiedName = rInnerClass.getIdQualifiedName(idValue);
		if (idQualifiedName==null) {
			idQualifiedName =  androidRInnerClass.getIdQualifiedName(idValue);
		}
		return idQualifiedName;
	}

	@Override
	public boolean containsField(String name) {
		return rInnerClass.containsField(name) ||  androidRInnerClass.containsField(name) ;
	}

	@Override
	public String getIdQualifiedName(String name) {
		String idQualifiedName = rInnerClass.getIdQualifiedName(name);
		if (idQualifiedName==null) {
			idQualifiedName =  androidRInnerClass.getIdQualifiedName(name);
		}
		return idQualifiedName;
	}
}