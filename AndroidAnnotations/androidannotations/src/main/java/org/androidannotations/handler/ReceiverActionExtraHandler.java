package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EReceiverHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

public class ReceiverActionExtraHandler extends BaseAnnotationHandler<EReceiverHolder> {

	public ReceiverActionExtraHandler(ProcessingEnvironment processingEnvironment) {
		super(ReceiverAction.Extra.class, processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingMethodHasAnnotation(ReceiverAction.class, element, validatedElements, valid);

		validatorHelper.canBePutInABundle(element, valid);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) throws Exception {
		// We don't do anything here.
	}

	public static JVar extractedFieldInExtra(VariableElement param, JVar extras, JBlock extrasNotNullBlock, EReceiverHolder holder, APTCodeModelHelper codeModelHelper, AnnotationHelper annotationHelper) {
		String paramName = param.getSimpleName().toString();
		JClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType(), holder);

		ReceiverAction.Extra annotation = param.getAnnotation(ReceiverAction.Extra.class);
		if (annotation != null && !annotation.value().isEmpty()) {
			paramName = annotation.value();
		}

		String extraParamName = paramName.replaceAll("\\.", "_") + "_Extra";
		JFieldVar paramVar = getStaticExtraField(holder, paramName);
		BundleHelper bundleHelper = new BundleHelper(annotationHelper, param);

		JExpression getExtraExpression = JExpr.invoke(extras, bundleHelper.getMethodNameToRestore()).arg(paramVar);
		if (bundleHelper.restoreCallNeedCastStatement()) {
			getExtraExpression = JExpr.cast(extraParamClass, getExtraExpression);

			if (bundleHelper.restoreCallNeedsSuppressWarning()) {
				JMethod onHandleIntentMethod = holder.getOnReceiveMethod();
				if (onHandleIntentMethod.annotations().size() == 0) {
					onHandleIntentMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
				}
			}
		}
		return extrasNotNullBlock.decl(extraParamClass, extraParamName, getExtraExpression);
	}

	private static JFieldVar getStaticExtraField(EReceiverHolder holder, String extraName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, extraName, "Extra");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, holder.classes().STRING, staticFieldName, lit(extraName));
		}
		return staticExtraField;
	}
}
