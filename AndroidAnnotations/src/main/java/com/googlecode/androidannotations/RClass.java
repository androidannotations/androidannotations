package com.googlecode.androidannotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

public class RClass {

	public enum Res {
		LAYOUT, ID;
	}
	
	private final Map<String, RInnerClass> rClass = new HashMap<String, RInnerClass>();

	public RClass(TypeElement rClassElement) {
		List<TypeElement> rInnerTypeElements = extractRInnerTypeElements(rClassElement);

		for (TypeElement rInnerTypeElement : rInnerTypeElements) {
			RInnerClass rInnerClass = new RInnerClass(rInnerTypeElement);
			rClass.put(rInnerTypeElement.getSimpleName().toString(), rInnerClass);
		}
	}

	private List<TypeElement> extractRInnerTypeElements(TypeElement rClassElement) {
		List<? extends Element> rEnclosedElements = rClassElement.getEnclosedElements();
		return ElementFilter.typesIn(rEnclosedElements);
	}

	public RInnerClass get(Res res) {
		
		String id = res.toString().toLowerCase();
		
		RInnerClass rInnerClass = rClass.get(id);
		if (rInnerClass != null) {
			return rInnerClass;
		} else {
			return RInnerClass.EMPTY_R_INNER_CLASS;
		}
	}

}
