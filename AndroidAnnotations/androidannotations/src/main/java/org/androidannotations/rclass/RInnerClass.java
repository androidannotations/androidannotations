/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.rclass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.helper.CaseHelper;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldRef;

public class RInnerClass implements IRInnerClass {

	private final Map<Integer, String> idQualifiedNamesByIdValues = new HashMap<>();
	private final Set<String> idQualifiedNames = new HashSet<>();

	private final String rInnerQualifiedName;

	public RInnerClass(TypeElement rInnerTypeElement) {
		if (rInnerTypeElement != null) {

			rInnerQualifiedName = rInnerTypeElement.getQualifiedName().toString();

			List<? extends Element> idEnclosedElements = rInnerTypeElement.getEnclosedElements();

			List<VariableElement> idFields = ElementFilter.fieldsIn(idEnclosedElements);

			for (VariableElement idField : idFields) {
				TypeKind fieldType = idField.asType().getKind();
				if (fieldType.isPrimitive() && fieldType.equals(TypeKind.INT)) {
					String idQualifiedName = rInnerQualifiedName + "." + idField.getSimpleName();
					idQualifiedNames.add(idQualifiedName);
					Integer idFieldId = (Integer) idField.getConstantValue();
					if (idFieldId != null) {
						idQualifiedNamesByIdValues.put(idFieldId, idQualifiedName);
					}
				}
			}
		} else {
			rInnerQualifiedName = "";
		}
	}

	@Override
	public boolean containsIdValue(Integer idValue) {
		return idQualifiedNamesByIdValues.containsKey(idValue);
	}

	@Override
	public String getIdQualifiedName(Integer idValue) {
		return idQualifiedNamesByIdValues.get(idValue);
	}

	@Override
	public boolean containsField(String name) {
		boolean containsField = idQualifiedNames.contains(rInnerQualifiedName + "." + name);

		if (!containsField) {
			String snakeCaseName = CaseHelper.camelCaseToSnakeCase(name);
			containsField = idQualifiedNames.contains(rInnerQualifiedName + "." + snakeCaseName);
		}

		return containsField;
	}

	@Override
	public String getIdQualifiedName(String name) {
		String idQualifiedName = rInnerQualifiedName + "." + name;

		if (idQualifiedNames.contains(idQualifiedName)) {
			return idQualifiedName;
		} else {
			String snakeCaseName = CaseHelper.camelCaseToSnakeCase(name);
			idQualifiedName = rInnerQualifiedName + "." + snakeCaseName;
			if (idQualifiedNames.contains(idQualifiedName)) {
				return idQualifiedName;
			} else {
				return null;
			}
		}
	}

	@Override
	public JFieldRef getIdStaticRef(Integer idValue, ProcessHolder holder) {
		String layoutFieldQualifiedName = getIdQualifiedName(idValue);
		return extractIdStaticRef(holder, layoutFieldQualifiedName);
	}

	@Override
	public JFieldRef getIdStaticRef(String name, ProcessHolder holder) {
		String layoutFieldQualifiedName = getIdQualifiedName(name);
		return extractIdStaticRef(holder, layoutFieldQualifiedName);
	}

	public static JFieldRef extractIdStaticRef(ProcessHolder holder, String layoutFieldQualifiedName) {
		if (layoutFieldQualifiedName != null) {
			int fieldSuffix = layoutFieldQualifiedName.lastIndexOf('.');
			String fieldName = layoutFieldQualifiedName.substring(fieldSuffix + 1);
			String rInnerClassName = layoutFieldQualifiedName.substring(0, fieldSuffix);

			JClass refClass = holder.refClass(rInnerClassName);
			return refClass.staticRef(fieldName);
		} else {
			return null;
		}
	}
}
