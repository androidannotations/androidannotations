package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;

public interface HasTarget {
	
	Class<? extends Annotation> getTarget();

}
