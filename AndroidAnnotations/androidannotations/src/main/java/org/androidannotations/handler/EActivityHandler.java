package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;

public class EActivityHandler extends BaseAnnotationHandler<EActivityHolder> implements GeneratingAnnotationHandler<EActivityHolder> {

	private AnnotationHelper annotationHelper;

	public EActivityHandler(ProcessingEnvironment processingEnvironment) {
		super(EActivity.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public EActivityHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EActivityHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsActivity(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EActivityHolder holder) {

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, getTarget(), rClass.get(IRClass.Res.LAYOUT), false);

		JFieldRef contentViewId = null;
		if (fieldRefs.size() == 1) {
			contentViewId = fieldRefs.get(0);
		}

		if (contentViewId != null) {
			JBlock onCreateBody = holder.getOnCreate().body();
			JMethod setContentView = holder.getSetContentViewLayout();
			onCreateBody.invoke(setContentView).arg(contentViewId);
		}
	}
}
