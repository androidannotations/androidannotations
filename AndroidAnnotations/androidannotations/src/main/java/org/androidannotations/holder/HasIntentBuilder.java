package org.androidannotations.holder;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

public interface HasIntentBuilder extends GeneratedClassHolder {

	public void setIntentBuilderClass(JDefinedClass intentBuilderClass);
	public JDefinedClass getIntentBuilderClass();

	public void setIntentField(JFieldVar intentField);
	public JFieldVar getIntentField();
}
