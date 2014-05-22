/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.ActionBarSherlockHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.HoloEverywhereHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.*;

public class EFragmentHolder extends EComponentWithViewSupportHolder implements HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasReceiverRegistration {

	private JFieldVar contentView;
	private JBlock setContentViewBlock;
	private JVar inflater;
	private JVar container;
	private JDefinedClass fragmentBuilderClass;
	private JFieldRef fragmentArgumentsBuilderField;
	private JMethod injectArgsMethod;
	private JBlock injectArgsBlock;
	private JVar injectBundleArgs;
	private InstanceStateHolder instanceStateHolder;
	private OnActivityResultHolder onActivityResultHolder;
	private ReceiverRegistrationHolder receiverRegistrationHolder;
	private JBlock onCreateOptionsMenuMethodBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;
	private JVar onOptionsItemSelectedItem;
	private JVar onOptionsItemSelectedItemId;
	private JBlock onOptionsItemSelectedIfElseBlock;
	private JBlock onCreateAfterSuperBlock;
	private JBlock onDestroyBeforeSuperBlock;
	private JBlock onStartAfterSuperBlock;
	private JBlock onStopBeforeSuperBlock;
	private JBlock onResumeAfterSuperBlock;
	private JBlock onPauseBeforeSuperBlock;
	private JBlock onAttachAfterSuperBlock;
	private JBlock onDetachBeforeSuperBlock;

	public EFragmentHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		instanceStateHolder = new InstanceStateHolder(this);
		onActivityResultHolder = new OnActivityResultHolder(this);
		receiverRegistrationHolder = new ReceiverRegistrationHolder(this);
		setOnCreate();
		setOnViewCreated();
		setFragmentBuilder();
	}

	private void setOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JVar onCreateSavedInstanceState = onCreate.param(classes().BUNDLE, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();

		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		setFindViewById();
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		onCreateAfterSuperBlock = onCreateBody.block();
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	private void setOnViewCreated() {
		JMethod onViewCreated = generatedClass.method(PUBLIC, codeModel().VOID, "onViewCreated");
		onViewCreated.annotate(Override.class);
		JVar view = onViewCreated.param(classes().VIEW, "view");
		JVar savedInstanceState = onViewCreated.param(classes().BUNDLE, "savedInstanceState");
		JBlock onViewCreatedBody = onViewCreated.body();
		onViewCreatedBody.invoke(_super(), onViewCreated).arg(view).arg(savedInstanceState);
		viewNotifierHelper.invokeViewChanged(onViewCreatedBody);
	}

	private void setFindViewById() {
		JMethod findViewById = generatedClass.method(PUBLIC, classes().VIEW, "findViewById");
		findViewById.annotate(Override.class);

		JVar idParam = findViewById.param(codeModel().INT, "id");

		JBlock body = findViewById.body();

		JFieldVar contentView = getContentView();

		body._if(contentView.eq(_null())) //
				._then()._return(_null());

		body._return(contentView.invoke(findViewById).arg(idParam));
	}

	private void setFragmentBuilder() throws JClassAlreadyExistsException {
		fragmentBuilderClass = generatedClass._class(PUBLIC | STATIC, "FragmentBuilder_");
		JClass superClass = refClass(org.androidannotations.api.builder.FragmentBuilder.class);
		superClass = superClass.narrow(fragmentBuilderClass);
		fragmentBuilderClass._extends(superClass);
		fragmentArgumentsBuilderField = ref("args");
		setFragmentBuilderBuild();
		setFragmentBuilderCreate();
	}

	private void setFragmentBuilderBuild() {
		JMethod method = fragmentBuilderClass.method(PUBLIC, generatedClass._extends(), "build");
		JBlock body = method.body();

		JVar fragment = body.decl(generatedClass, "fragment_", _new(generatedClass));
		body.invoke(fragment, "setArguments").arg(fragmentArgumentsBuilderField);
		body._return(fragment);
	}

	private void setFragmentBuilderCreate() {
		JMethod method = generatedClass.method(STATIC | PUBLIC, fragmentBuilderClass, "builder");
		method.body()._return(_new(fragmentBuilderClass));
	}

	private void setOnCreateOptionsMenu() {
		JClass menuClass = classes().MENU;
		JClass menuInflaterClass = classes().MENU_INFLATER;
		if (usesActionBarSherlock()) {
			menuClass = classes().SHERLOCK_MENU;
			menuInflaterClass = classes().SHERLOCK_MENU_INFLATER;
		}

		JMethod method = generatedClass.method(PUBLIC, codeModel().VOID, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(menuClass, "menu");
		onCreateOptionsMenuMenuInflaterVar = method.param(menuInflaterClass, "inflater");
		onCreateOptionsMenuMethodBody = methodBody.block();
		methodBody.invoke(_super(), method).arg(onCreateOptionsMenuMenuParam).arg(onCreateOptionsMenuMenuInflaterVar);

		getInitBody().invoke("setHasOptionsMenu").arg(JExpr.TRUE);
	}

	private void setOnOptionsItemSelected() {
		JClass menuItemClass = classes().MENU_ITEM;
		if (usesActionBarSherlock()) {
			menuItemClass = classes().SHERLOCK_MENU_ITEM;
		}

		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onOptionsItemSelected");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onOptionsItemSelectedItem = method.param(menuItemClass, "item");
		JVar handled = methodBody.decl(codeModel().BOOLEAN, "handled", invoke(_super(), method).arg(onOptionsItemSelectedItem));
		methodBody._if(handled)._then()._return(TRUE);
		onOptionsItemSelectedItemId = methodBody.decl(codeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedIfElseBlock = methodBody.block();
		methodBody._return(FALSE);
	}

	private boolean usesActionBarSherlock() {
		return new ActionBarSherlockHelper(new AnnotationHelper(processingEnvironment())).usesActionBarSherlock(this);
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr.invoke("getActivity");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		init.param(classes().BUNDLE, "savedInstanceState");
	}

	public JFieldVar getContentView() {
		if (contentView == null) {
			setContentView();
		}
		return contentView;
	}

	private void setContentView() {
		contentView = generatedClass.field(PRIVATE, classes().VIEW, "contentView_");
	}

	private void setOnCreateView() {
		JMethod onCreateView = generatedClass.method(PUBLIC, classes().VIEW, "onCreateView");
		onCreateView.annotate(Override.class);

		HoloEverywhereHelper holoEverywhereHelper = new HoloEverywhereHelper(this);
		JClass inflaterClass;
		if (holoEverywhereHelper.usesHoloEverywhere()) {
			inflaterClass = classes().HOLO_EVERYWHERE_LAYOUT_INFLATER;
		} else {
			inflaterClass = classes().LAYOUT_INFLATER;
		}

		inflater = onCreateView.param(inflaterClass, "inflater");
		container = onCreateView.param(classes().VIEW_GROUP, "container");

		JVar savedInstanceState = onCreateView.param(classes().BUNDLE, "savedInstanceState");

		JBlock body = onCreateView.body();
		setContentViewBlock = body.block();

		body._return(contentView);
	}

	private void setOnStart() {
		JMethod onStart = generatedClass.method(PUBLIC, codeModel().VOID, "onStart");
		onStart.annotate(Override.class);
		JBlock onStartBody = onStart.body();
		onStartBody.invoke(_super(), onStart);
		onStartAfterSuperBlock = onStartBody.block();
	}

	private void setOnAttach() {
		JMethod onAttach = generatedClass.method(PUBLIC, codeModel().VOID, "onAttach");
		onAttach.annotate(Override.class);
		JVar activityParam = onAttach.param(classes().ACTIVITY, "activity");
		JBlock onAttachBody = onAttach.body();
		onAttachBody.invoke(_super(), onAttach).arg(activityParam);
		onAttachAfterSuperBlock = onAttachBody.block();
	}

	private void setOnResume() {
		JMethod onResume = generatedClass.method(PUBLIC, codeModel().VOID, "onResume");
		onResume.annotate(Override.class);
		JBlock onResumeBody = onResume.body();
		onResumeBody.invoke(_super(), onResume);
		onResumeAfterSuperBlock = onResumeBody.block();
	}

	private void setOnPause() {
		JMethod onPause = generatedClass.method(PUBLIC, codeModel().VOID, "onPause");
		onPause.annotate(Override.class);
		JBlock onPauseBody = onPause.body();
		onPauseBeforeSuperBlock = onPauseBody.block();
		onPauseBody.invoke(_super(), onPause);
	}

	private void setOnDetach() {
		JMethod onDetach = generatedClass.method(PUBLIC, codeModel().VOID, "onDetach");
		onDetach.annotate(Override.class);
		JBlock onDetachBody = onDetach.body();
		onDetachBeforeSuperBlock = onDetachBody.block();
		onDetachBody.invoke(_super(), onDetach);
	}

	private void setOnStop() {
		JMethod onStop = generatedClass.method(PUBLIC, codeModel().VOID, "onStop");
		onStop.annotate(Override.class);
		JBlock onStopBody = onStop.body();
		onStopBeforeSuperBlock = onStopBody.block();
		onStopBody.invoke(_super(), onStop);
	}

	private void setOnDestroy() {
		JMethod onDestroy = generatedClass.method(PUBLIC, codeModel().VOID, "onDestroy");
		onDestroy.annotate(Override.class);
		JBlock onDestroyBody = onDestroy.body();
		onDestroyBeforeSuperBlock = onDestroyBody.block();
		onDestroyBody.invoke(_super(), onDestroy);
	}

	public JBlock getSetContentViewBlock() {
		if (setContentViewBlock == null) {
			setOnCreateView();
		}
		return setContentViewBlock;
	}

	public JVar getInflater() {
		if (inflater == null) {
			setOnCreateView();
		}
		return inflater;
	}

	public JVar getContainer() {
		if (container == null) {
			setOnCreateView();
		}
		return container;
	}

	public JDefinedClass getBuilderClass() {
		return fragmentBuilderClass;
	}

	public JFieldRef getBuilderArgsField() {
		return fragmentArgumentsBuilderField;
	}

	public JMethod getInjectArgsMethod() {
		if (injectArgsMethod == null) {
			setInjectArgs();
		}
		return injectArgsMethod;
	}

	public JBlock getInjectArgsBlock() {
		if (injectArgsBlock == null) {
			setInjectArgs();
		}
		return injectArgsBlock;
	}

	public JVar getInjectBundleArgs() {
		if (injectBundleArgs == null) {
			setInjectArgs();
		}
		return injectBundleArgs;
	}

	private void setInjectArgs() {
		injectArgsMethod = generatedClass.method(PRIVATE, codeModel().VOID, "injectFragmentArguments_");
		JBlock injectExtrasBody = injectArgsMethod.body();
		injectBundleArgs = injectExtrasBody.decl(classes().BUNDLE, "args_", invoke("getArguments"));
		injectArgsBlock = injectExtrasBody._if(injectBundleArgs.ne(_null()))._then();

		getInitBody().invoke(injectArgsMethod);
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		return instanceStateHolder.getSaveStateMethodBody();
	}

	@Override
	public JVar getSaveStateBundleParam() {
		return instanceStateHolder.getSaveStateBundleParam();
	}

	@Override
	public JMethod getRestoreStateMethod() {
		return instanceStateHolder.getRestoreStateMethod();
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		return instanceStateHolder.getRestoreStateBundleParam();
	}

	@Override
	public JBlock getOnCreateOptionsMenuMethodBody() {
		if (onCreateOptionsMenuMethodBody == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMethodBody;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuInflaterVar() {
		if (onCreateOptionsMenuMenuInflaterVar == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMenuInflaterVar;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuParam() {
		if (onCreateOptionsMenuMenuParam == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMenuParam;
	}

	@Override
	public JVar getOnOptionsItemSelectedItem() {
		if (onOptionsItemSelectedItem == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItem;
	}

	@Override
	public JVar getOnOptionsItemSelectedItemId() {
		if (onOptionsItemSelectedItemId == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItemId;
	}

	@Override
	public JBlock getOnOptionsItemSelectedIfElseBlock() {
		if (onOptionsItemSelectedIfElseBlock == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedIfElseBlock;
	}

	@Override
	public JBlock getOnActivityResultCaseBlock(int requestCode) {
		return onActivityResultHolder.getCaseBlock(requestCode);
	}

	@Override
	public JVar getOnActivityResultDataParam() {
		return onActivityResultHolder.getDataParam();
	}

	@Override
	public JVar getOnActivityResultResultCodeParam() {
		return onActivityResultHolder.getResultCodeParam();
	}

	@Override
	public JFieldVar getIntentFilterField(String[] actions) {
		return receiverRegistrationHolder.getIntentFilterField(actions);
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		if (onCreateAfterSuperBlock == null) {
			setOnCreate();
		}
		return onCreateAfterSuperBlock;
	}

	@Override
	public JBlock getOnDestroyBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			setOnDestroy();
		}
		return onDestroyBeforeSuperBlock;
	}

	@Override
	public JBlock getOnStartAfterSuperBlock() {
		if (onStartAfterSuperBlock == null) {
			setOnStart();
		}
		return onStartAfterSuperBlock;
	}

	@Override
	public JBlock getOnStopBeforeSuperBlock() {
		if (onStopBeforeSuperBlock == null) {
			setOnStop();
		}
		return onStopBeforeSuperBlock;
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		if (onResumeAfterSuperBlock == null) {
			setOnResume();
		}
		return onResumeAfterSuperBlock;
	}

	@Override
	public JBlock getOnPauseBeforeSuperBlock() {
		if (onPauseBeforeSuperBlock == null) {
			setOnPause();
		}
		return onPauseBeforeSuperBlock;
	}

	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		if (onAttachAfterSuperBlock == null) {
			setOnAttach();
		}
		return onAttachAfterSuperBlock;
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		if (onDetachBeforeSuperBlock == null) {
			setOnDetach();
		}
		return onDetachBeforeSuperBlock;
	}
}
