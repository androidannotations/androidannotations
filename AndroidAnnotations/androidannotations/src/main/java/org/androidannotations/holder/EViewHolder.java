package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.*;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;

public class EViewHolder extends EComponentWithViewSupportHolder {

	protected static final String ALREADY_INFLATED_COMMENT = "" // +
			+ "The mAlreadyInflated_ hack is needed because of an Android bug\n" // +
			+ "which leads to infinite calls of onFinishInflate()\n" //
			+ "when inflating a layout with a parent and using\n" //
			+ "the <merge /> tag." //
			;

	private static final String SUPPRESS_WARNING_COMMENT = "" //
			+ "We use @SuppressWarning here because our java code\n" //
			+ "generator doesn't know that there is no need\n" //
			+ "to import OnXXXListeners from View as we already\n" //
			+ "are in a View." //
			;

	protected JMethod onFinishInflate;
	protected JFieldVar alreadyInflated;

	public EViewHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		addSuppressWarning();
		createConstructorAndBuilder();
	}

	@Override
	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		String generatedBeanQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;
		JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);

		int modifiers;
		if (annotatedElement.getModifiers().contains(Modifier.ABSTRACT)) {
			modifiers = JMod.PUBLIC | JMod.ABSTRACT;
		} else {
			modifiers = JMod.PUBLIC | JMod.FINAL;
		}

		generatedClass = codeModel()._class(modifiers, generatedBeanQualifiedName, ClassType.CLASS);
		generatedClass._extends(annotatedComponent);
	}

	private void addSuppressWarning() {
		generatedClass.annotate(SuppressWarnings.class).param("value", "unused");
		generatedClass.javadoc().append(SUPPRESS_WARNING_COMMENT);
	}

	private void createConstructorAndBuilder() {
		List<ExecutableElement> constructors = new ArrayList<ExecutableElement>();
		for (Element e : annotatedElement.getEnclosedElements()) {
			if (e.getKind() == CONSTRUCTOR) {
				constructors.add((ExecutableElement) e);
			}
		}

		for (ExecutableElement userConstructor : constructors) {
			JMethod copyConstructor = generatedClass.constructor(PUBLIC);
			JMethod staticHelper = generatedClass.method(PUBLIC | STATIC, generatedClass._extends(), "build");
			JBlock body = copyConstructor.body();
			JInvocation superCall = body.invoke("super");
			JInvocation newInvocation = JExpr._new(generatedClass);
			for (VariableElement param : userConstructor.getParameters()) {
				String paramName = param.getSimpleName().toString();
				String paramType = param.asType().toString();
				copyConstructor.param(refClass(paramType), paramName);
				staticHelper.param(refClass(paramType), paramName);
				superCall.arg(JExpr.ref(paramName));
				newInvocation.arg(JExpr.ref(paramName));
			}

			JVar newCall = staticHelper.body().decl(generatedClass, "instance", newInvocation);
			staticHelper.body().invoke(newCall, getOnFinishInflate());
			staticHelper.body()._return(newCall);
			body.invoke(getInit());
		}
	}

	@Override
	protected void setContextRef() {
		contextRef = invoke("getContext");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		viewNotifierHelper.wrapInitWithNotifier();
	}

	public JMethod getOnFinishInflate() {
		if (onFinishInflate == null) {
			setOnFinishInflate();
		}
		return onFinishInflate;
	}

	protected void setOnFinishInflate() {
		onFinishInflate = generatedClass.method(PUBLIC, codeModel().VOID, "onFinishInflate");
		onFinishInflate.annotate(Override.class);
		onFinishInflate.javadoc().append(ALREADY_INFLATED_COMMENT);

		JBlock ifNotInflated = onFinishInflate.body()._if(getAlreadyInflated().not())._then();
		ifNotInflated.assign(getAlreadyInflated(), JExpr.TRUE);

		getInit();
		viewNotifierHelper.invokeViewChanged(ifNotInflated);

		onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");
	}

	public JFieldVar getAlreadyInflated() {
		if (alreadyInflated == null) {
			setAlreadyInflated();
		}
		return alreadyInflated;
	}

	private void setAlreadyInflated() {
		alreadyInflated = generatedClass.field(PRIVATE, JType.parse(codeModel(), "boolean"), "alreadyInflated_", JExpr.FALSE);
	}
}
