package org.androidannotations.helper;

import org.androidannotations.holder.EComponentHolder;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

public class HoloEverywhereHelper {

	private EComponentHolder holder;

	public HoloEverywhereHelper(EComponentHolder holder) {
		this.holder = holder;
	}

	public boolean usesHoloEverywhere() {
		TypeElement typeElement = holder.getAnnotatedElement();

		TypeMirror superType;
		while (!((superType = typeElement.getSuperclass()) instanceof NoType)) {
			typeElement = (TypeElement) ((DeclaredType) superType).asElement();
			String qName = typeElement.getQualifiedName().toString();
			if (qName.startsWith("org.holoeverywhere")) {
				return true;
			}
		}
		return false;
	}
}
