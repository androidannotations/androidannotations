package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;

public interface HasOnActivityResult extends GeneratedClassHolder {
	JBlock getOnActivityResultCaseBlock(int requestCode);
	JVar getOnActivityResultDataParam();
	JVar getOnActivityResultResultCodeParam();
}
