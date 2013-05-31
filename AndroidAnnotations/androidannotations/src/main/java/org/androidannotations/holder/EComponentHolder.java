package org.androidannotations.holder;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

public abstract class EComponentHolder extends BaseGeneratedClassHolder {


	protected JExpression contextRef;
	protected JMethod init;
	private JVar resourcesRef;

	public EComponentHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
        super(processHolder, annotatedElement);
	}

	public JExpression getContextRef() {
		if (contextRef == null) {
			setContextRef();
		}
		return contextRef;
	}

	protected abstract void setContextRef();

	public JMethod getInit() {
		if (init == null) {
			setInit();
		}
		return init;
	}

	protected abstract void setInit();

	public JVar getResourcesRef() {
		if (resourcesRef == null) {
			setResourcesRef();
		}
		return resourcesRef;
	}

	private void setResourcesRef()  {
		resourcesRef = getInit().body().decl(classes().RESOURCES, "resources_", getContextRef().invoke("getResources"));
	}
}
