/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;

public class ParcelerHelper extends AnnotationHelper {

	private static final Map<String, Integer> SUPPORTED_PARCEL_TYPES = new HashMap<>();

	static {
		SUPPORTED_PARCEL_TYPES.put(Collection.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(List.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(ArrayList.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(Set.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(HashSet.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(TreeSet.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(CanonicalNameConstants.SPARSE_ARRAY, 1);
		SUPPORTED_PARCEL_TYPES.put(Map.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(HashMap.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(TreeMap.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(Integer.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Long.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Double.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Float.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Byte.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(String.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Character.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(Boolean.class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(byte[].class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(char[].class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(boolean[].class.getName(), 0);
		SUPPORTED_PARCEL_TYPES.put(CanonicalNameConstants.IBINDER, 0);
		SUPPORTED_PARCEL_TYPES.put(CanonicalNameConstants.BUNDLE, 0);
		SUPPORTED_PARCEL_TYPES.put(CanonicalNameConstants.SPARSE_BOOLEAN_ARRAY, 0);
		SUPPORTED_PARCEL_TYPES.put(LinkedList.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(LinkedHashMap.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(SortedMap.class.getName(), 2);
		SUPPORTED_PARCEL_TYPES.put(SortedSet.class.getName(), 1);
		SUPPORTED_PARCEL_TYPES.put(LinkedHashSet.class.getName(), 1);
	}

	public ParcelerHelper(AndroidAnnotationsEnvironment environment) {
		super(environment);
	}

	public boolean isParcelType(TypeMirror typeMirror) {
		return isParcelType(typeMirror, true);
	}

	public boolean isParcelType(TypeMirror typeMirror, boolean root) {
		if (typeMirror instanceof DeclaredType && getElementUtils().getTypeElement(CanonicalNameConstants.PARCEL_ANNOTATION) != null) {
			DeclaredType declaredType = (DeclaredType) typeMirror;
			TypeElement element = (TypeElement) declaredType.asElement();

			String name = element.getQualifiedName().toString();

			if (isAnnotatedWith(element, CanonicalNameConstants.PARCEL_ANNOTATION)) {
				return true;
			}

			if (SUPPORTED_PARCEL_TYPES.containsKey(name)) {
				boolean genericsMatch = true;

				Integer genericsSize = SUPPORTED_PARCEL_TYPES.get(name);
				if (genericsSize == declaredType.getTypeArguments().size() && (!root || genericsSize > 0)) {
					for (int i = 0; i < genericsSize; i++) {
						genericsMatch &= isParcelType(declaredType.getTypeArguments().get(i), false);
					}

					return genericsMatch;
				}
			}
		}
		return false;
	}
}
