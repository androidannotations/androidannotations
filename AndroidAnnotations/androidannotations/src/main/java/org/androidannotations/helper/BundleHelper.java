/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import static org.androidannotations.helper.CanonicalNameConstants.BUNDLE;
import static org.androidannotations.helper.CanonicalNameConstants.CHAR_SEQUENCE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class BundleHelper {
	private static final Map<String, String> methodSuffixNameByTypeName = new HashMap<String, String>();

	static {

		methodSuffixNameByTypeName.put(BUNDLE, "Bundle");

		methodSuffixNameByTypeName.put("boolean", "Boolean");
		methodSuffixNameByTypeName.put("boolean[]", "BooleanArray");

		methodSuffixNameByTypeName.put("byte", "Byte");
		methodSuffixNameByTypeName.put("byte[]", "ByteArray");

		methodSuffixNameByTypeName.put("char", "Char");
		methodSuffixNameByTypeName.put("char[]", "CharArray");

		methodSuffixNameByTypeName.put(CHAR_SEQUENCE, "CharSequence");

		methodSuffixNameByTypeName.put("double", "Double");
		methodSuffixNameByTypeName.put("double[]", "DoubleArray");

		methodSuffixNameByTypeName.put("float", "Float");
		methodSuffixNameByTypeName.put("float[]", "FloatArray");

		methodSuffixNameByTypeName.put("int", "Int");
		methodSuffixNameByTypeName.put("int[]", "IntArray");
		methodSuffixNameByTypeName.put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");

		methodSuffixNameByTypeName.put("long", "Long");
		methodSuffixNameByTypeName.put("long[]", "LongArray");

		methodSuffixNameByTypeName.put("short", "Short");
		methodSuffixNameByTypeName.put("short[]", "ShortArray");

		methodSuffixNameByTypeName.put(STRING, "String");
		methodSuffixNameByTypeName.put("java.lang.String[]", "StringArray");
		methodSuffixNameByTypeName.put("java.util.ArrayList<java.lang.String>", "StringArrayList");
	}

	private AnnotationHelper annotationHelper;

	private boolean restoreCallNeedCastStatement = false;
	private boolean restoreCallNeedsSuppressWarning = false;

	private String methodNameToSave;
	private String methodNameToRestore;

	public BundleHelper(AnnotationHelper helper, Element element) {
		annotationHelper = helper;

		String typeString = element.asType().toString();
		TypeElement elementType = annotationHelper.typeElementFromQualifiedName(typeString);

		if (methodSuffixNameByTypeName.containsKey(typeString)) {

			methodNameToSave = "put" + methodSuffixNameByTypeName.get(typeString);
			methodNameToRestore = "get" + methodSuffixNameByTypeName.get(typeString);

		} else if (element.asType().getKind() == TypeKind.ARRAY) {

			ArrayType arrayType = (ArrayType) element.asType();

			boolean hasTypeArguments = false;
			if (arrayType.getComponentType() instanceof DeclaredType) {
				DeclaredType declaredType = (DeclaredType) arrayType.getComponentType();
				typeString = declaredType.asElement().toString();
				hasTypeArguments = declaredType.getTypeArguments().size() > 0;
			} else {
				typeString = arrayType.getComponentType().toString();
			}

			elementType = annotationHelper.typeElementFromQualifiedName(typeString);

			if (isTypeParcelable(elementType)) {
				methodNameToSave = "put" + "ParcelableArray";
				methodNameToRestore = "get" + "ParcelableArray";
				restoreCallNeedCastStatement = true;

				if (hasTypeArguments) {
					restoreCallNeedsSuppressWarning = true;
				}
			} else {
				methodNameToSave = "put" + "Serializable";
				methodNameToRestore = "get" + "Serializable";
				restoreCallNeedCastStatement = true;
			}
		} else {

			TypeMirror elementAsType = element.asType();
			boolean hasTypeArguments = false;
			if (elementAsType instanceof DeclaredType) {
				DeclaredType declaredType = (DeclaredType) elementAsType;
				typeString = declaredType.asElement().toString();
				elementType = annotationHelper.typeElementFromQualifiedName(typeString);
				hasTypeArguments = declaredType.getTypeArguments().size() > 0;
			}

			if (isTypeParcelable(elementType)) {
				methodNameToSave = "put" + "Parcelable";
				methodNameToRestore = "get" + "Parcelable";
			} else {
				methodNameToSave = "put" + "Serializable";
				methodNameToRestore = "get" + "Serializable";
				restoreCallNeedCastStatement = true;

				if (hasTypeArguments) {
					restoreCallNeedsSuppressWarning = true;
				}
			}
		}
	}

	public boolean restoreCallNeedCastStatement() {
		return restoreCallNeedCastStatement;
	}

	public boolean restoreCallNeedsSuppressWarning() {
		return restoreCallNeedsSuppressWarning;
	}

	public String getMethodNameToSave() {
		return methodNameToSave;
	}

	public String getMethodNameToRestore() {
		return methodNameToRestore;
	}

	private boolean isTypeParcelable(TypeElement elementType) {

		TypeElement parcelableType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.PARCELABLE);

		return elementType != null && annotationHelper.isSubtype(elementType, parcelableType);
	}
}
