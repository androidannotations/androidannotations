package com.googlecode.androidannotations.processing;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.helper.HasTarget;
import com.googlecode.androidannotations.model.MetaModel;

public interface ElementProcessor extends HasTarget{
	
	void process(Element element, MetaModel metaModel);

}
