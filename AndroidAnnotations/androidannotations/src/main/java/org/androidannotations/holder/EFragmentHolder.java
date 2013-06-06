package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.HoloEverywhereHelper;
import org.androidannotations.helper.ThirdPartyLibHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.*;

public class EFragmentHolder extends EComponentWithViewSupportHolder implements HasInstanceState, HasOptionsMenu, HasOnActivityResult {

	private JFieldVar contentView;
	private JBlock setContentViewBlock;
	private JVar inflater;
	private JVar container;
	private JDefinedClass fragmentBuilderClass;
	private JFieldVar fragmentArgumentsBuilderField;
	private JMethod injectArgsMethod;
	private JBlock injectArgsBlock;
	private JVar injectBundleArgs;
    private InstanceStateHolder instanceStateHolder;
	private OnActivityResultHolder onActivityResultHolder;
	private JBlock onCreateOptionsMenuMethodBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;
	private JVar onOptionsItemSelectedItem;
	private JVar onOptionsItemSelectedItemId;
	private JBlock onOptionsItemSelectedIfElseBlock;

	public EFragmentHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
        instanceStateHolder = new InstanceStateHolder(this);
		onActivityResultHolder = new OnActivityResultHolder(this);
		createOnCreate();
		createOnViewCreated();
		createFragmentBuilder();
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JVar onCreateSavedInstanceState = onCreate.param(classes().BUNDLE, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();

		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		createFindViewById();
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	private void createOnViewCreated() {
		JMethod onViewCreated = generatedClass.method(PUBLIC, codeModel().VOID, "onViewCreated");
		onViewCreated.annotate(Override.class);
		JVar view = onViewCreated.param(classes().VIEW, "view");
		JVar savedInstanceState = onViewCreated.param(classes().BUNDLE, "savedInstanceState");
		JBlock onViewCreatedBody = onViewCreated.body();
		onViewCreatedBody.invoke(_super(), onViewCreated).arg(view).arg(savedInstanceState);
		viewNotifierHelper.invokeViewChanged(onViewCreatedBody);
	}

	private void createFindViewById() {
		JMethod findViewById = generatedClass.method(PUBLIC, classes().VIEW, "findViewById");
		JVar idParam = findViewById.param(codeModel().INT, "id");

		JBlock body = findViewById.body();

		JFieldVar contentView = getContentView();

		body._if(contentView.eq(_null())) //
				._then()._return(_null());

		body._return(contentView.invoke(findViewById).arg(idParam));
	}

	private void createFragmentBuilder() throws JClassAlreadyExistsException {
		fragmentBuilderClass = generatedClass._class(PUBLIC | STATIC, "FragmentBuilder_");
		fragmentArgumentsBuilderField = fragmentBuilderClass.field(PRIVATE, classes().BUNDLE, "args_");
		createFragmentBuilderConstructor();
		createFragmentBuilderBuild();
		createFragmentBuilderCreate();
	}

	private void createFragmentBuilderConstructor() {
		JMethod constructor = fragmentBuilderClass.constructor(PRIVATE);
		JBlock constructorBody = constructor.body();
		constructorBody.assign(fragmentArgumentsBuilderField, _new(classes().BUNDLE));
	}

	private void createFragmentBuilderBuild() {
		JMethod method = fragmentBuilderClass.method(PUBLIC, generatedClass._extends(), "build");
		JBlock body = method.body();

		JVar fragment = body.decl(generatedClass, "fragment_", _new(generatedClass));
		body.invoke(fragment, "setArguments").arg(fragmentArgumentsBuilderField);
		body._return(fragment);
	}

	private void createFragmentBuilderCreate() {
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

		getInit().body().invoke("setHasOptionsMenu").arg(JExpr.TRUE);
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
		return new ThirdPartyLibHelper(new AnnotationHelper(processingEnvironment())).usesActionBarSherlock(this);
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
			setOnCreateView();
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
		body.assign(contentView, _super().invoke(onCreateView).arg(inflater).arg(container).arg(savedInstanceState));

		setContentViewBlock = body.block();

		body._return(contentView);
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

	public JFieldVar getBuilderArgsField() {
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

		getInit().body().invoke(injectArgsMethod);
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
}
