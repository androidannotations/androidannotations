package org.androidannotations.helper;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JMethod;
import org.androidannotations.holder.HasIntentBuilder;

import static com.sun.codemodel.JMod.PUBLIC;

public class ServiceIntentBuilder extends IntentBuilder {

	public ServiceIntentBuilder(HasIntentBuilder holder) {
		super(holder);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();
		createStart();
		createStop();
	}

	private void createStart() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.classes().COMPONENT_NAME, "start");
		method.body()._return(contextField.invoke("startService").arg(holder.getIntentField()));
	}

	private void createStop() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().BOOLEAN, "stop");
		method.body()._return(contextField.invoke("stopService").arg(holder.getIntentField()));
	}

}
