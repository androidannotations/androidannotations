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

import static org.androidannotations.helper.CanonicalNameConstants.BUNDLE;
import static org.androidannotations.helper.CanonicalNameConstants.CHAR_SEQUENCE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;

public class BundleHelper {
	public static final Map<String, String> METHOD_SUFFIX_BY_TYPE_NAME = new HashMap<>();

	static {

		METHOD_SUFFIX_BY_TYPE_NAME.put(BUNDLE, "Bundle");

		METHOD_SUFFIX_BY_TYPE_NAME.put("boolean", "Boolean");
		METHOD_SUFFIX_BY_TYPE_NAME.put("boolean[]", "BooleanArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put("byte", "Byte");
		METHOD_SUFFIX_BY_TYPE_NAME.put("byte[]", "ByteArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put("char", "Char");
		METHOD_SUFFIX_BY_TYPE_NAME.put("char[]", "CharArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put(CHAR_SEQUENCE, "CharSequence");
		METHOD_SUFFIX_BY_TYPE_NAME.put(CHAR_SEQUENCE + "[]", "CharSequenceArray");
		METHOD_SUFFIX_BY_TYPE_NAME.put("java.util.ArrayList<" + CHAR_SEQUENCE + ">", "CharSequenceArrayList");

		METHOD_SUFFIX_BY_TYPE_NAME.put("double", "Double");
		METHOD_SUFFIX_BY_TYPE_NAME.put("double[]", "DoubleArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put("float", "Float");
		METHOD_SUFFIX_BY_TYPE_NAME.put("float[]", "FloatArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put("int", "Int");
		METHOD_SUFFIX_BY_TYPE_NAME.put("int[]", "IntArray");
		METHOD_SUFFIX_BY_TYPE_NAME.put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");

		METHOD_SUFFIX_BY_TYPE_NAME.put("long", "Long");
		METHOD_SUFFIX_BY_TYPE_NAME.put("long[]", "LongArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put("short", "Short");
		METHOD_SUFFIX_BY_TYPE_NAME.put("short[]", "ShortArray");

		METHOD_SUFFIX_BY_TYPE_NAME.put(STRING, "String");
		METHOD_SUFFIX_BY_TYPE_NAME.put("java.lang.String[]", "StringArray");
		METHOD_SUFFIX_BY_TYPE_NAME.put("java.util.ArrayList<java.lang.String>", "StringArrayList");
	}

	private AndroidAnnotationsEnvironment environment;
	private AnnotationHelper annotationHelper;
	private APTCodeModelHelper codeModelHelper;

	private boolean restoreCallNeedCastStatement = false;
	private boolean restoreCallNeedsSuppressWarning = false;
	private boolean parcelerBean = false;

	private String methodNameToSave;
	private String methodNameToRestore;

	private TypeMirror upperBound;

	public BundleHelper(AndroidAnnotationsEnvironment environment, TypeMirror element) {
		this.environment = environment;
		annotationHelper = new AnnotationHelper(environment);
		codeModelHelper = new APTCodeModelHelper(environment);

		String typeString = element.toString();
		TypeMirror type = element;

		if (METHOD_SUFFIX_BY_TYPE_NAME.containsKey(typeString)) {

			methodNameToSave = "put" + METHOD_SUFFIX_BY_TYPE_NAME.get(typeString);
			methodNameToRestore = "get" + METHOD_SUFFIX_BY_TYPE_NAME.get(typeString);

		} else if (element.getKind() == TypeKind.ARRAY) {

			ArrayType arrayType = (ArrayType) element;

			boolean hasTypeArguments = false;
			if (arrayType.getComponentType() instanceof DeclaredType) {
				DeclaredType declaredType = (DeclaredType) arrayType.getComponentType();
				type = declaredType;
				hasTypeArguments = declaredType.getTypeArguments().size() > 0;
			} else if (arrayType.getComponentType().getKind() == TypeKind.TYPEVAR) {
				type = arrayType.getComponentType();
				upperBound = getUpperBound(type);
				restoreCallNeedCastStatement = true;
				restoreCallNeedsSuppressWarning = true;
			} else {
				type = arrayType.getComponentType();
			}

			if (isTypeParcelable(type)) {
				methodNameToSave = "put" + "ParcelableArray";
				methodNameToRestore = "get" + "ParcelableArray";

				if (hasTypeArguments) {
					restoreCallNeedsSuppressWarning = true;
				}
			} else {
				methodNameToSave = "put" + "Serializable";
				methodNameToRestore = "get" + "Serializable";
				restoreCallNeedCastStatement = true;
			}
		} else if (typeString.startsWith(CanonicalNameConstants.ARRAYLIST)) {

			boolean hasTypeArguments = false;
			if (element instanceof DeclaredType) {
				DeclaredType declaredType = (DeclaredType) element;
				List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
				if (typeArguments.size() == 1) {
					TypeMirror typeArgument = typeArguments.get(0);
					if (typeArgument instanceof DeclaredType) {
						declaredType = (DeclaredType) typeArgument;
						type = declaredType;
						hasTypeArguments = declaredType.getTypeArguments().size() > 0;
					}
					if (isTypeParcelable(type)) {
						methodNameToSave = "put" + "ParcelableArrayList";
						methodNameToRestore = "get" + "ParcelableArrayList";

						if (hasTypeArguments) {
							restoreCallNeedsSuppressWarning = true;
						}
					}
				}
			}

			if (methodNameToSave == null) {
				methodNameToSave = "put" + "Serializable";
				methodNameToRestore = "get" + "Serializable";
				restoreCallNeedCastStatement = true;
				restoreCallNeedsSuppressWarning = true;
			}
		} else if (typeString.startsWith(CanonicalNameConstants.SPARSE_ARRAY)) {
			methodNameToSave = "put" + "SparseParcelableArray";
			methodNameToRestore = "get" + "SparseParcelableArray";
		} else {

			boolean hasTypeArguments = element.getKind() == TypeKind.DECLARED && hasTypeArguments(element) || //
					element.getKind() == TypeKind.TYPEVAR && hasTypeArguments(getUpperBound(element));

			ParcelerHelper parcelerHelper = new ParcelerHelper(environment);

			if (isTypeParcelable(type)) {
				methodNameToSave = "put" + "Parcelable";
				methodNameToRestore = "get" + "Parcelable";
			} else if (parcelerHelper.isParcelType(type)) {
				methodNameToSave = "put" + "Parcelable";
				methodNameToRestore = "get" + "Parcelable";
				parcelerBean = true;
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

	private boolean isTypeParcelable(TypeMirror typeMirror) {
		TypeMirror parcelableType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.PARCELABLE).asType();
		return annotationHelper.isSubtype(typeMirror, parcelableType);
	}

	private TypeMirror getUpperBound(TypeMirror type) {
		TypeVariable typeVariable = (TypeVariable) type;
		return typeVariable.getUpperBound();
	}

	private boolean hasTypeArguments(TypeMirror type) {
		DeclaredType declaredType = (DeclaredType) type;
		return declaredType.getTypeArguments().size() > 0;
	}

	public IJExpression getExpressionToRestoreFromBundle(AbstractJClass variableClass, IJExpression bundle, IJExpression extraKey, JMethod method) {
		IJExpression expressionToRestore;
		if ("getParcelableArray".equals(methodNameToRestore)) {
			AbstractJClass erasure;
			if (upperBound != null) {
				erasure = codeModelHelper.typeMirrorToJClass(upperBound).erasure().array();
			} else {
				erasure = variableClass.elementType().erasure().array();
			}
			expressionToRestore = environment.getJClass(org.androidannotations.api.bundle.BundleHelper.class).staticInvoke("getParcelableArray").arg(bundle).arg(extraKey).arg(erasure.dotclass());
		} else {
			expressionToRestore = JExpr.invoke(bundle, methodNameToRestore).arg(extraKey);
		}

		if (parcelerBean) {
			expressionToRestore = environment.getJClass(CanonicalNameConstants.PARCELS_UTILITY_CLASS).staticInvoke("unwrap").arg(expressionToRestore);
		}

		if (restoreCallNeedCastStatement) {
			expressionToRestore = JExpr.cast(variableClass, expressionToRestore);

			if (restoreCallNeedsSuppressWarning) {
				codeModelHelper.addSuppressWarnings(method, "unchecked");
			}
		}
		return expressionToRestore;
	}

	public IJStatement getExpressionToSaveFromField(IJExpression saveStateBundleParam, IJExpression fieldName, IJExpression variableRef) {
		IJExpression refExpression = variableRef;
		if (parcelerBean) {
			refExpression = environment.getJClass(CanonicalNameConstants.PARCELS_UTILITY_CLASS).staticInvoke("wrap").arg(refExpression);
		}
		return JExpr.invoke(saveStateBundleParam, methodNameToSave).arg(fieldName).arg(refExpression);
	}
}
