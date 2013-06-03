package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.OptionsMenuItem;
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

import static com.sun.codemodel.JExpr.ref;

public class OptionsMenuItemHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	private IdAnnotationHelper annotationHelper;

	public OptionsMenuItemHandler(ProcessingEnvironment processingEnvironment) {
		super(OptionsMenuItem.class, processingEnvironment);
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

		validatorHelper.isDeclaredType(element, valid);

		validatorHelper.extendsMenuItem(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		String fieldName = element.getSimpleName().toString();
		JBlock body = holder.getOnCreateOptionsMenuMethodBody();
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam();

		JFieldRef idsRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.ID, true);
		body.assign(ref(fieldName), menuParam.invoke("findItem").arg(idsRef));
	}
}
