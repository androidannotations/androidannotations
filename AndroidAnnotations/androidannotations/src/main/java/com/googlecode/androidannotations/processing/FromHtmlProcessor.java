package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class FromHtmlProcessor implements ElementProcessor {

	private final IRClass rClass;

	public FromHtmlProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FromHtml.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {
		ActivityHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

		String fieldName = element.getSimpleName().toString();

		FromHtml annotation = element.getAnnotation(FromHtml.class);
		int idValue = annotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.STRING);
		JFieldRef idRef;
		if (idValue == Id.DEFAULT_VALUE) {
			// TODO: Implement a default value behavior
			throw new UnsupportedOperationException("The default value support for FromHtml is not supported yet");
		} else {
			idRef = rInnerClass.getIdStaticRef(idValue, holder);
		}

		JBlock methodBody = holder.afterSetContentView.body();

		methodBody._if(JExpr.ref(fieldName).ne(JExpr._null()))._then().invoke(JExpr.ref(fieldName), "setText").arg(holder.refClass("android.text.Html").staticInvoke("fromHtml").arg(JExpr.invoke("getString").arg(idRef)));
	}
}