package org.androidannotations.helper;

import com.sun.codemodel.*;
import org.androidannotations.holder.HasIntentBuilder;

import static com.sun.codemodel.JMod.PUBLIC;

public class ActivityIntentBuilder extends IntentBuilder {

	public ActivityIntentBuilder(HasIntentBuilder holder) {
		super(holder);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();
		createStart();
		createStartForResult();
	}

	private void createStart() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "start");
		method.body().invoke(contextField, "startActivity").arg(holder.getIntentField());
	}

	private void createStartForResult() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "startForResult");
		JVar requestCode = method.param(holder.codeModel().INT, "requestCode");

		JBlock body = method.body();
		JClass activityClass = holder.classes().ACTIVITY;
		JConditional condition = body._if(contextField._instanceof(activityClass));
		condition._then() //
				.invoke(JExpr.cast(activityClass, contextField), "startActivityForResult").arg(holder.getIntentField()).arg(requestCode);
		condition._else() //
				.invoke(contextField, "startActivity").arg(holder.getIntentField());
	}
}
