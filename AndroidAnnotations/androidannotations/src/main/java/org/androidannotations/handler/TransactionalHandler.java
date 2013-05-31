package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class TransactionalHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public TransactionalHandler(ProcessingEnvironment processingEnvironment) {
		super(Transactional.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.doesntThrowException(executableElement, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.param.hasOneOrTwoParametersAndFirstIsDb(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;

		String returnTypeName = executableElement.getReturnType().toString();
		JClass returnType = holder.refClass(returnTypeName);

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		codeModelHelper.removeBody(method);

		JVar db = method.params().get(0);

		JBlock body = method.body();

		body.invoke(db, "beginTransaction");

		JTryBlock tryBlock = body._try();

		JExpression activitySuper = holder.getGeneratedClass().staticRef("super");
		JInvocation superCall = JExpr.invoke(activitySuper, method);

		for (JVar param : method.params()) {
			superCall.arg(param);
		}
		JBlock tryBody = tryBlock.body();
		if (returnTypeName.equals("void")) {
			tryBody.add(superCall);
			tryBody.invoke(db, "setTransactionSuccessful");
			tryBody._return();
		} else {
			JVar result = tryBody.decl(returnType, "result_", superCall);
			tryBody.invoke(db, "setTransactionSuccessful");
			tryBody._return(result);
		}

		JCatchBlock catchBlock = tryBlock._catch(holder.refClass(RuntimeException.class));

		JVar exceptionParam = catchBlock.param("e");

		JBlock catchBody = catchBlock.body();

		JInvocation errorInvoke = catchBody.staticInvoke(holder.classes().LOG, "e");

		errorInvoke.arg(holder.getGeneratedClass().name());
		errorInvoke.arg("Error in transaction");
		errorInvoke.arg(exceptionParam);

		catchBody._throw(exceptionParam);

		tryBlock._finally().invoke(db, "endTransaction");
	}
}
