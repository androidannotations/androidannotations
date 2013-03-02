package org.androidannotations.processing;

import static com.sun.codemodel.JExpr.invoke;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ViewChangedHolder {

	private JMethod onViewChanged;
	private JVar onViewChangedHasViewsParam;

	public ViewChangedHolder(JMethod onViewChanged, JVar onViewChangedHasViewsParam) {
		this.onViewChanged = onViewChanged;
		this.onViewChangedHasViewsParam = onViewChangedHasViewsParam;
	}

	public JBlock body() {
		return onViewChanged.body();
	}

	public JInvocation findViewById(JFieldRef idRef) {
		JInvocation findViewById = invoke(onViewChangedHasViewsParam, "findViewById");
		findViewById.arg(idRef);
		return findViewById;

	}

}
