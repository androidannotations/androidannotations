package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public interface HasExtras extends GeneratedClassHolder {
	JMethod getInjectExtrasMethod();
	JBlock getInjectExtrasBlock();
	JVar getInjectExtras();
}
