package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.sun.codemodel.JExpr.TRUE;
import static com.sun.codemodel.JExpr.invoke;

public class OptionsItemHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	private IdAnnotationHelper annotationHelper;

	public OptionsItemHandler(ProcessingEnvironment processingEnvironment) {
		super(OptionsItem.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		validatorHelper.uniqueId(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.zeroOrOneMenuItemParameter(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		boolean hasItemParameter = parameters.size() == 1;

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, IRClass.Res.ID, true);
		JExpression ifExpr = holder.getOnOptionsItemSelectedItemId().eq(idsRefs.get(0));
		for (int i = 1; i < idsRefs.size(); i++) {
			ifExpr = ifExpr.cor(holder.getOnOptionsItemSelectedItemId().eq(idsRefs.get(i)));
		}

		JBlock itemIfBody = holder.getOnOptionsItemSelectedIfElseBlock()._if(ifExpr)._then();
		JInvocation methodCall = invoke(methodName);

		if (returnMethodResult) {
			itemIfBody._return(methodCall);
		} else {
			itemIfBody.add(methodCall);
			itemIfBody._return(TRUE);
		}

		if (hasItemParameter) {
			methodCall.arg(holder.getOnOptionsItemSelectedItem());
		}
	}
}
