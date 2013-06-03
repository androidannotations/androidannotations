package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;

public interface HasOptionsMenu extends GeneratedClassHolder {
	JBlock getOnCreateOptionsMenuMethodBody();
	JVar getOnCreateOptionsMenuMenuInflaterVar();
	JVar getOnCreateOptionsMenuMenuParam();
}
