package org.androidannotations.handler;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EViewGroupHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EViewGroupHandler extends BaseAnnotationHandler<EViewGroupHolder> implements GeneratingAnnotationHandler<EViewGroupHolder> {

	private IdAnnotationHelper annotationHelper;

	public EViewGroupHandler(ProcessingEnvironment processingEnvironment) {
		super(EViewGroup.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public EViewGroupHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EViewGroupHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsViewGroup(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.isNotFinal(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EViewGroupHolder holder) {
		JFieldRef contentViewId = annotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.LAYOUT, false);
		if (contentViewId != null) {
            holder.getSetContentViewBlock().invoke("inflate").arg(holder.getContextRef()).arg(contentViewId).arg(JExpr._this());
		}
	}
}
