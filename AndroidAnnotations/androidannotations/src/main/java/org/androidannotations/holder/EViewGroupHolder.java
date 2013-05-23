package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JMod.PUBLIC;

public class EViewGroupHolder extends EViewHolder {

	private JBlock setContentViewBlock;

	public EViewGroupHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	protected void setOnFinishInflate() {
		onFinishInflate = generatedClass.method(PUBLIC, codeModel().VOID, "onFinishInflate");
		onFinishInflate.annotate(Override.class);
		onFinishInflate.javadoc().append(ALREADY_INFLATED_COMMENT);

		JBlock ifNotInflated = onFinishInflate.body()._if(getAlreadyInflated().not())._then();
		ifNotInflated.assign(getAlreadyInflated(), JExpr.TRUE);

		setContentViewBlock = ifNotInflated.block();

		getInit();
		viewNotifierHelper.invokeViewChanged(ifNotInflated);

		onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");
	}

	public JBlock getSetContentViewBlock() {
		if (setContentViewBlock == null) {
			setOnFinishInflate();
		}
		return setContentViewBlock;
	}
}
