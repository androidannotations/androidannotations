package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr.FALSE;
import static com.sun.codemodel.JExpr._null;

public class EFragmentHandler extends BaseAnnotationHandler<EFragmentHolder> implements GeneratingAnnotationHandler<EFragmentHolder> {

	public EFragmentHandler(ProcessingEnvironment processingEnvironment) {
		super(EFragment.class, processingEnvironment);
	}

	@Override
	public EFragmentHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EFragmentHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.hasEmptyConstructor(element, valid);

		validatorHelper.extendsFragment(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {

		IdAnnotationHelper idAnnotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);

		JFieldRef contentViewId = idAnnotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.LAYOUT, false);

		if (contentViewId != null) {

			JBlock block = holder.getSetContentViewBlock();
			JVar inflater = holder.getInflater();
			JVar container  = holder.getContainer();

			JFieldVar contentView = holder.getContentView();

			block._if(contentView.eq(_null())) //
					._then() //
					.assign(contentView, inflater.invoke("inflate").arg(contentViewId).arg(container).arg(FALSE));
		}

	}
}
