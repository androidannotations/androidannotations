package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.SaveOnActivityDestroy;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.processing.SaveOnActivityDestroyProcessor;

public class SaveOnActivityDestroyValidator extends AnnotationHelper implements ElementValidator {
	
	public SaveOnActivityDestroyValidator(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return SaveOnActivityDestroy.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		
		String typeString = element.asType().toString();

		if (! isTrivialType(typeString)) {
			TypeElement elementType = typeElementFromQualifiedName(typeString);
			
			if (elementType == null) {
				elementType = getArrayEnclosingType(typeString);
				if (elementType == null) {
					printAnnotationError(element, getTarget(), "Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable");
					return false;
				}
			}

			TypeElement parcelableType = typeElementFromQualifiedName("android.os.Parcelable");
			TypeElement serializableType = typeElementFromQualifiedName("java.io.Serializable");
			if (! isSubtype(elementType, parcelableType) && ! isSubtype(elementType, serializableType)) {
				printAnnotationError(element, getTarget(), "Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable");
				return false;
			}
		}
		
		return true;
	}

	private TypeElement getArrayEnclosingType(String typeString) {
		typeString = typeString.replace("[]", "");
		return typeElementFromQualifiedName(typeString);
	}
	
	private boolean isTrivialType(String type) {
		return SaveOnActivityDestroyProcessor.methodSuffixNameByTypeName.containsKey(type);
	}

}
