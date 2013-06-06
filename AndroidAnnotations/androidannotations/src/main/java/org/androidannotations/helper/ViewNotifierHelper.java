package org.androidannotations.helper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.holder.EComponentHolder;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;

public class ViewNotifierHelper {

	private EComponentHolder holder;
	private JFieldVar notifier;

	public ViewNotifierHelper(EComponentHolder holder) {
		this.holder = holder;
	}

	public void invokeViewChanged(JBlock block) {
		block.invoke(notifier, "notifyViewChanged").arg(_this());
	}

	public JVar replacePreviousNotifier(JBlock block) {
		JClass notifierClass = holder.refClass(OnViewChangedNotifier.class);
		if (notifier == null) {
			notifier = holder.getGeneratedClass().field(PRIVATE | FINAL, notifierClass, "onViewChangedNotifier_", _new(notifierClass));
			holder.getGeneratedClass()._implements(HasViews.class);
		}
		return block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(notifier));
	}

	public JVar replacePreviousNotifierWithNull(JBlock block) {
		JClass notifierClass = holder.refClass(OnViewChangedNotifier.class);
		return block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(_null()));
	}

	public void resetPreviousNotifier(JBlock block, JVar previousNotifier) {
		JClass notifierClass = holder.refClass(OnViewChangedNotifier.class);
		block.staticInvoke(notifierClass, "replaceNotifier").arg(previousNotifier);
	}

	public void wrapInitWithNotifier() {
		JBlock initBlock = holder.getInit().body();
		JVar previousNotifier = replacePreviousNotifier(initBlock);
		resetPreviousNotifier(initBlock, previousNotifier);
	}

}
