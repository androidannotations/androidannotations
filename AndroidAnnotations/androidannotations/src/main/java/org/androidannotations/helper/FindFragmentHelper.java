package org.androidannotations.helper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.holder.EComponentHolder;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.PRIVATE;

public class FindFragmentHelper {

	public static JMethod createFindNativeFragmentById(EComponentHolder holder) {
		JMethod method = holder.getGeneratedClass().method(PRIVATE, holder.classes().FRAGMENT, "findNativeFragmentById");
		JVar idParam = method.param(holder.codeModel().INT, "id");

		JBlock body = method.body();

		body._if(holder.getContextRef()._instanceof(holder.classes().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(holder.classes().ACTIVITY, "activity_", cast(holder.classes().ACTIVITY, holder.getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));

		return method;
	}

	public static JMethod createFindSupportFragmentById(EComponentHolder holder) {
		JMethod method = holder.getGeneratedClass().method(PRIVATE, holder.classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		JVar idParam = method.param(holder.codeModel().INT, "id");

		JBlock body = method.body();

		body._if(holder.getContextRef()._instanceof(holder.classes().FRAGMENT_ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(holder.classes().FRAGMENT_ACTIVITY, "activity_", cast(holder.classes().FRAGMENT_ACTIVITY, holder.getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));

		return method;
	}

	public static JMethod createFindNativeFragmentByTag(EComponentHolder holder) {
		JMethod method = holder.getGeneratedClass().method(PRIVATE, holder.classes().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = method.param(holder.classes().STRING, "tag");

		JBlock body = method.body();

		body._if(holder.getContextRef()._instanceof(holder.classes().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(holder.classes().ACTIVITY, "activity_", cast(holder.classes().ACTIVITY, holder.getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));

		return method;
	}

	public static JMethod createFindSupportFragmentByTag(EComponentHolder holder) {
		JMethod method = holder.getGeneratedClass().method(PRIVATE, holder.classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
		JVar tagParam = method.param(holder.classes().STRING, "tag");

		JBlock body = method.body();

		body._if(holder.getContextRef()._instanceof(holder.classes().FRAGMENT_ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(holder.classes().FRAGMENT_ACTIVITY, "activity_", cast(holder.classes().FRAGMENT_ACTIVITY, holder.getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));

		return method;
	}
}
