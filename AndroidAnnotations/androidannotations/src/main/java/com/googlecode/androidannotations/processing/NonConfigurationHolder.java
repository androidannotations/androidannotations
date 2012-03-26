package com.googlecode.androidannotations.processing;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class NonConfigurationHolder {
	
	public JDefinedClass holderClass;

	public JMethod holderConstructor;

	public JInvocation newHolder;

	public JBlock initIfNonConfiguration;

	public JVar initNonConfigurationInstance;

}
