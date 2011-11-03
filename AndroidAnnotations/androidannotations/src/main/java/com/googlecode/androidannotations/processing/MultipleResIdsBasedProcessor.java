package com.googlecode.androidannotations.processing;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JFieldRef;

/**
 * @author Mathieu Boniface
 */
public abstract class MultipleResIdsBasedProcessor {

	private final IRClass rClass;

	public MultipleResIdsBasedProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	protected List<JFieldRef> extractQualifiedIds(Element element, int [] idsValues, String methodSuffix, EBeanHolder holder) {

		List<JFieldRef> idsRefs = new ArrayList<JFieldRef>();
		IRInnerClass rInnerClass = rClass.get(Res.ID);

		if (idsValues.length == 1 && idsValues[0] == Id.DEFAULT_VALUE) {
			
			String fieldName = element.getSimpleName().toString();
			int lastIndex = fieldName.lastIndexOf(methodSuffix);
			
			if (lastIndex != -1) {
				fieldName = fieldName.substring(0, lastIndex);
			}
			
			JFieldRef idRef = rInnerClass.getIdStaticRef(fieldName, holder);
			idsRefs.add(idRef);

		} else {
			for (int idValue : idsValues) {

				JFieldRef idRef = rInnerClass.getIdStaticRef(idValue, holder);
				idsRefs.add(idRef);
			
			}
		}
		return idsRefs;
	}

}
