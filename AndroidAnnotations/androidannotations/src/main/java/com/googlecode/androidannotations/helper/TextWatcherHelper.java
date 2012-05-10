package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.processing.EBeanHolder;
import com.googlecode.androidannotations.processing.TextWatcherHolder;
import com.googlecode.androidannotations.rclass.IRClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class TextWatcherHelper extends IdAnnotationHelper {

	private final APTCodeModelHelper codeModelHelper;

	public TextWatcherHelper(//
			ProcessingEnvironment processingEnv, //
			Class<? extends Annotation> target, //
			IRClass rClass, //
			APTCodeModelHelper codeModelHelper) {

		super(processingEnv, target, rClass);

		this.codeModelHelper = codeModelHelper;

	}

	public TextWatcherHolder getOrCreateListener(JCodeModel codeModel, EBeanHolder holder, JFieldRef idRef, TypeMirror viewParameterType) {

		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		TextWatcherHolder textWatcherHolder = holder.textWatchers.get(idRefString);

		if (textWatcherHolder == null) {
			JClass editableClass = holder.refClass("android.text.Editable");
			JClass charSequenceClass = holder.refClass("java.lang.CharSequence");

			JDefinedClass onTextChangeListenerClass = codeModel.anonymousClass(holder.refClass("android.text.TextWatcher"));

			JMethod afterTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "afterTextChanged");
			afterTextChangedMethod.param(editableClass, "s");
			afterTextChangedMethod.annotate(Override.class);

			JMethod onTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onTextChanged");
			onTextChangedMethod.param(charSequenceClass, "s");
			onTextChangedMethod.param(codeModel.INT, "start");
			onTextChangedMethod.param(codeModel.INT, "before");
			onTextChangedMethod.param(codeModel.INT, "count");
			onTextChangedMethod.annotate(Override.class);

			JMethod beforeTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "beforeTextChanged");
			beforeTextChangedMethod.param(charSequenceClass, "s");
			beforeTextChangedMethod.param(codeModel.INT, "start");
			beforeTextChangedMethod.param(codeModel.INT, "count");
			beforeTextChangedMethod.param(codeModel.INT, "after");
			beforeTextChangedMethod.annotate(Override.class);

			JBlock block = holder.afterSetContentView.body().block();

			JClass viewClass;
			if (viewParameterType != null) {
				String viewParameterTypeString = viewParameterType.toString();
				viewClass = holder.refClass(viewParameterTypeString);
			} else {
				viewClass = holder.refClass("android.widget.TextView");
			}
			JExpression findViewById = JExpr.cast(viewClass, JExpr.invoke("findViewById").arg(idRef));

			JVar viewVariable = block.decl(JMod.FINAL, viewClass, "view", findViewById);
			block._if(viewVariable.ne(JExpr._null()))._then().invoke(viewVariable, "addTextChangedListener").arg(JExpr._new(onTextChangeListenerClass));

			textWatcherHolder = new TextWatcherHolder(//
					afterTextChangedMethod, //
					beforeTextChangedMethod, //
					onTextChangedMethod, //
					viewVariable);

			holder.textWatchers.put(idRefString, textWatcherHolder);
		}

		return textWatcherHolder;
	}


}
