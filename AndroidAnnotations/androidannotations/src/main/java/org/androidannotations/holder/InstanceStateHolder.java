package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.process.ProcessHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class InstanceStateHolder implements HasInstanceState {

    private EComponentHolder holder;
    private JBlock saveStateMethodBody;
    private JVar saveStateBundleParam;
    private JMethod restoreStateMethod;
    private JVar restoreStateBundleParam;

    public InstanceStateHolder(EComponentHolder holder) {
        this.holder = holder;
    }

    @Override
    public JBlock getSaveStateMethodBody() {
        if (saveStateMethodBody == null) {
            setSaveStateMethod();
        }
        return saveStateMethodBody;
    }

    @Override
    public JVar getSaveStateBundleParam() {
        if (saveStateBundleParam == null) {
            setSaveStateMethod();
        }
        return saveStateBundleParam;
    }

    private void setSaveStateMethod() {
        JMethod method = getGeneratedClass().method(PUBLIC, codeModel().VOID, "onSaveInstanceState");
        method.annotate(Override.class);
        saveStateBundleParam = method.param(classes().BUNDLE, "bundle");

        saveStateMethodBody = method.body();

        saveStateMethodBody.invoke(JExpr._super(), "onSaveInstanceState").arg(saveStateBundleParam);
    }

    @Override
    public JMethod getRestoreStateMethod() {
        if (restoreStateMethod == null) {
            setRestoreStateMethod();
        }
        return restoreStateMethod;
    }

    @Override
    public JVar getRestoreStateBundleParam() {
        if (restoreStateBundleParam == null) {
            setRestoreStateMethod();
        }
        return restoreStateBundleParam;
    }

    private void setRestoreStateMethod() {
        restoreStateMethod = getGeneratedClass().method(PRIVATE, codeModel().VOID, "restoreSavedInstanceState_");
        restoreStateBundleParam = restoreStateMethod.param(classes().BUNDLE, "savedInstanceState");
        getInit().body().invoke(restoreStateMethod).arg(restoreStateBundleParam);

        restoreStateMethod.body() //
                ._if(ref("savedInstanceState").eq(_null())) //
                ._then()._return();
    }

	public JMethod getInit() {
		return holder.getInit();
	}

	@Override
    public JDefinedClass getGeneratedClass() {
        return holder.getGeneratedClass();
    }

	@Override
	public JCodeModel codeModel() {
		return holder.codeModel();
	}

	@Override
	public ProcessHolder.Classes classes() {
		return holder.classes();
	}

	@Override
	public TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	@Override
	public ProcessingEnvironment processingEnvironment() {
		return holder.processingEnvironment();
	}

	@Override
	public JClass refClass(String fullyQualifiedClassName) {
		return holder.refClass(fullyQualifiedClassName);
	}

	@Override
	public JClass refClass(Class<?> clazz) {
		return holder.refClass(clazz);
	}

	@Override
	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		return holder.definedClass(fullyQualifiedClassName);
	}

	@Override
	public void generateApiClass(Element originatingElement, Class<?> apiClass) {
		holder.generateApiClass(originatingElement, apiClass);
	}
}
