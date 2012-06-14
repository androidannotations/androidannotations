package com.googlecode.androidannotations.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class GenericTypeFinder {

	private static final String BASE_PACKAGE = "java.";

	private TypeMirror elementType;
	private AnnotationHelper helper;

	private GenericTypeFinder(TypeMirror elementType, AnnotationHelper helper) {
		this.elementType = elementType;
		this.helper = helper;
	}

	public static GenericTypeFinder newInstance(TypeMirror elementType, TargetAnnotationHelper helper) {
		return new GenericTypeFinder(elementType, helper);
	}

	public TypeMirror get(int ordinal) {
		List<TypeReference> references = new ArrayList<TypeReference>();
		resolveGenericTypes(elementType, BASE_PACKAGE, references);
		int lastIndex = ordinal;
		TypeReference last = null;

		for (TypeReference current : references) {
			if (lastIndex >= 0) {
				if (last != null) {
					TypeMirror typeParameter = last.genericTypes.get(lastIndex);
					if (current.genericTypes.size() == 0) {
						return typeParameter;
					}
					boolean match = false;
					for (int i = 0; i < current.parameterElements.size(); i++) {
						if (typeParameter.toString().equals(current.parameterElements.get(i).toString())) {
							lastIndex = i;
							match = true;
							break;
						}
					}
					if (!match) {
						return typeParameter;
					}

				}
				last = current;
			} else {
				return null;
			}
		}
		return last.genericTypes.get(lastIndex);
	}

	private void resolveGenericTypes(TypeMirror elementType, String tillPackage, List<TypeReference> references) {
		TypeElement typeElement = (TypeElement) helper.getTypeUtils().asElement(elementType);
		if (!typeElement.toString().startsWith(tillPackage)) {
			resolveGenericTypes(getCollectionInterface(typeElement), tillPackage, references);
		}
		references.add(new TypeReference(typeElement, elementType));
	}

	private TypeMirror getCollectionInterface(TypeElement typeElement) {
		TypeMirror superClass = typeElement.getSuperclass();
		if (helper.isAssignable(superClass, Collection.class)) {
			return superClass;
		}
		for (TypeMirror type : typeElement.getInterfaces()) {
			if (helper.isAssignable(type, Collection.class)) {
				return type;
			}
		}
		return null;
	}

	private static class TypeReference {
		public final List<? extends TypeParameterElement> parameterElements;
		public final List<? extends TypeMirror> genericTypes;

		public TypeReference(TypeElement typeElement, TypeMirror elementType) {
			parameterElements = typeElement.getTypeParameters();
			if (elementType instanceof DeclaredType) {
				genericTypes = ((DeclaredType) elementType).getTypeArguments();
				return;
			}
			genericTypes = Collections.emptyList();
		}
	}
}
