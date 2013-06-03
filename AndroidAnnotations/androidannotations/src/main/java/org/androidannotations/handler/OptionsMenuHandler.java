package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.OptionsMenu;
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
import java.util.List;

public class OptionsMenuHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	private IdAnnotationHelper annotationHelper;

	public OptionsMenuHandler(ProcessingEnvironment processingEnvironment) {
		super(OptionsMenu.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.hasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.MENU, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		JBlock body = holder.getOnCreateOptionsMenuMethodBody();
		JVar menuInflater = holder.getOnCreateOptionsMenuMenuInflaterVar();
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam();

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, IRClass.Res.MENU, false);
		for (JFieldRef optionsMenuRefId : fieldRefs) {
			body.invoke(menuInflater, "inflate").arg(optionsMenuRefId).arg(menuParam);
		}

	}
}
