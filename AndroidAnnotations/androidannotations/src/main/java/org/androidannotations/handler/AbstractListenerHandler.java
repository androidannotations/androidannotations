package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.sun.codemodel.JExpr.*;

public abstract class AbstractListenerHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper helper;
	private EComponentWithViewSupportHolder holder;
	private String methodName;

	public AbstractListenerHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
	}

	public AbstractListenerHandler(String target, ProcessingEnvironment processingEnvironment) {
		super(target, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		validatorHelper.uniqueId(element, validatedElements, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		this.holder = holder;

		this.methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, IRClass.Res.ID, true);

		JDefinedClass listenerAnonymousClass = holder.codeModel().anonymousClass(getListenerClass());
		JMethod listenerMethod = createListenerMethod(listenerAnonymousClass);
		listenerMethod.annotate(Override.class);

		JBlock listenerMethodBody = listenerMethod.body();

		JExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation call = invoke(activityRef, methodName);

		makeCall(listenerMethodBody, call, returnType);

		processParameters(listenerMethod, call, parameters);

		for (JFieldRef idRef : idsRefs) {
			JBlock block = holder.getOnViewChangedBody().block();

			JExpression findViewExpression = holder.findViewById(idRef);
			if (!getViewClass().equals(classes().VIEW)) {
				findViewExpression = cast(getViewClass(), findViewExpression);
			}

			JVar view = block.decl(getViewClass(), "view", findViewExpression);
			block._if(view.ne(_null()))._then().invoke(view, getSetterName()).arg(_new(listenerAnonymousClass));
		}
	}

	protected abstract void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType);

	protected abstract void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> userParameters);

	protected abstract JMethod createListenerMethod(JDefinedClass listenerAnonymousClass);

	protected abstract String getSetterName();

	protected abstract JClass getListenerClass();

	protected JType getViewClass() {
		return classes().VIEW;
	}

	protected ProcessHolder.Classes classes() {
		return holder.classes();
	}

	protected JCodeModel codeModel() {
		return holder.codeModel();
	}

	protected JClass refClass(String qualifiedClassName) {
		return holder.refClass(qualifiedClassName);
	}

	protected String getMethodName() {
		return methodName;
	}
}