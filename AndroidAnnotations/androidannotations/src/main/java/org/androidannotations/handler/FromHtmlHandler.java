package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.FromHtml;
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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;

public class FromHtmlHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public FromHtmlHandler(ProcessingEnvironment processingEnvironment) {
		super(FromHtml.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.hasViewByIdAnnotation(element, validatedElements, valid);

		validatorHelper.extendsTextView(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.STRING, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		ProcessHolder.Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.STRING, true);

		JBlock methodBody = holder.getOnViewChangedBody();
		methodBody //
				._if(ref(fieldName).ne(_null())) //
				._then() //
				.invoke(ref(fieldName), "setText").arg(classes.HTML.staticInvoke("fromHtml").arg(holder.getContextRef().invoke("getString").arg(idRef)));
	}
}
