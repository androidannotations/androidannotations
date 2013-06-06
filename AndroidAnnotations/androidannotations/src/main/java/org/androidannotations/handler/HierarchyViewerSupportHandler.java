package org.androidannotations.handler;

import com.sun.codemodel.JInvocation;
import org.androidannotations.annotations.HierarchyViewerSupport;
import org.androidannotations.api.ViewServer;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import static com.sun.codemodel.JExpr._this;

public class HierarchyViewerSupportHandler extends BaseAnnotationHandler<EActivityHolder> {

	public HierarchyViewerSupportHandler(ProcessingEnvironment processingEnvironment) {
		super(HierarchyViewerSupport.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.hasEActivity(element, validatedElements, valid);

		validatorHelper.isDebuggable(element, androidManifest, valid);

		validatorHelper.hasInternetPermission(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws Exception {
		holder.generateApiClass(element, ViewServer.class);

		JInvocation viewServerInvocation = holder.classes().VIEW_SERVER.staticInvoke("get").arg(_this());

		holder.getOnViewChangedBody().invoke(viewServerInvocation, "addWindow").arg(_this());
		holder.getOnDestroyAfterSuperBlock().invoke(viewServerInvocation, "removeWindow").arg(_this());
		holder.getOnResumeAfterSuperBlock().invoke(viewServerInvocation, "setFocusedWindow").arg(_this());
	}
}
