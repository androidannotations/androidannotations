package org.androidannotations.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JGenerifiable;

public class GenericUtils {

	public static void generifyAs(TypeElement template, JGenerifiable generifiable) {
		for (TypeParameterElement typeParameterElement : template.getTypeParameters()) {
			generifiable.generify(typeParameterElement.getSimpleName().toString());
		}
	}

	public static JClass generifyAs(JCodeModel codeModel, TypeElement template, JClass clazz) {
		for (TypeParameterElement typeParameterElement : template.getTypeParameters()) {
			clazz = clazz.narrow(codeModel.ref(typeParameterElement.getSimpleName().toString()));
		}
		
		return clazz;
	}

}
