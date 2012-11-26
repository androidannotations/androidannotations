package org.androidannotations.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

public class OriginatingElementsHolder {

	private final Map<String, List<Element>> originatingElementsByClassName = new HashMap<String, List<Element>>();

	public void addAsOriginatingElement(String qualifiedName, Element element) {
		List<Element> originatingElements = originatingElementsByClassName.get(qualifiedName);
		if (originatingElements == null) {
			originatingElements = new ArrayList<Element>();
			originatingElementsByClassName.put(qualifiedName, originatingElements);
		}
		originatingElements.add(element);
	}

	public Element[] getOriginatingElements(String className) {
		List<Element> originatingElements = originatingElementsByClassName.get(className);
		if (originatingElements == null) {
			return new Element[0];
		} else {
			return originatingElements.toArray(new Element[originatingElements.size()]);
		}
	}
}
