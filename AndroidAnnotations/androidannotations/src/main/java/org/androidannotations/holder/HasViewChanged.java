package org.androidannotations.holder;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;

public interface HasViewChanged extends GeneratedClassHolder {
	ViewChangedHolder getOnViewChangedHolder();
	JMethod getFindNativeFragmentById();
	JMethod getFindSupportFragmentById();
	JMethod getFindNativeFragmentByTag();
	JMethod getFindSupportFragmentByTag();
    JExpression getContextRef();
}
