package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;

import static com.sun.codemodel.JMod.PRIVATE;

public abstract class EComponentHolder extends BaseGeneratedClassHolder {

	protected JExpression contextRef;
	protected JMethod init;
	private JVar resourcesRef;
	private Map<TypeMirror, JFieldVar> databaseHelperRefs = new HashMap<TypeMirror, JFieldVar>();
	private JVar handler;

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

	public JFieldVar getDatabaseHelperRef(TypeMirror databaseHelperTypeMirror) {
		JFieldVar databaseHelperRef = databaseHelperRefs.get(databaseHelperTypeMirror);
		if (databaseHelperRef == null) {
			databaseHelperRef = setDatabaseHelperRef(databaseHelperTypeMirror);
		}
		return databaseHelperRef;
	}

	private JFieldVar setDatabaseHelperRef(TypeMirror databaseHelperTypeMirror) {
		JClass databaseHelperClass = refClass(databaseHelperTypeMirror.toString());
		String fieldName = CaseHelper.lowerCaseFirst(databaseHelperClass.name()) + ModelConstants.GENERATION_SUFFIX;
		JFieldVar databaseHelperRef = generatedClass.field(PRIVATE, databaseHelperClass, fieldName);
		databaseHelperRefs.put(databaseHelperTypeMirror, databaseHelperRef);

		JExpression dbHelperClass = databaseHelperClass.dotclass();
		getInit().body().assign(databaseHelperRef, //
				classes().OPEN_HELPER_MANAGER.staticInvoke("getHelper").arg(getContextRef()).arg(dbHelperClass));

		return databaseHelperRef;
	}

	public JVar getHandler() {
		if (handler == null) {
			setHandler();
		}
		return handler;
	}

	private void setHandler() {
		JClass handlerClass = classes().HANDLER;
		handler = generatedClass.field(JMod.PRIVATE, handlerClass, "handler_", JExpr._new(handlerClass));
	}
}
