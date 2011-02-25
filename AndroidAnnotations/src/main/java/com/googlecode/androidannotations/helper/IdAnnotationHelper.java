package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class IdAnnotationHelper extends TargetAnnotationHelper {
	
	private final IRClass rClass;

	public IdAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target, IRClass rClass) {
		super(processingEnv, target);
		this.rClass = rClass;
	}
	
	public String extractAnnotationQualifiedId(Element element) {
		int idValue = extractAnnotationValue(element);
		IRInnerClass rInnerClass = rClass.get(Res.ID);
		String clickQualifiedId;

		if (idValue == Id.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			int lastIndex = fieldName.lastIndexOf(actionName());
			if (lastIndex != -1) {
				fieldName = fieldName.substring(0, lastIndex);
			}
			clickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);

		} else {
			clickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
		}
		return clickQualifiedId;
	}
	
	boolean containsIdValue(Integer idValue, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsIdValue(idValue);
	}


	boolean containsField(String name, Res res) {
		IRInnerClass rInnerClass = rClass.get(res);
		return rInnerClass.containsField(name);
	}

}
