package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.FindFragmentHelper;
import org.androidannotations.helper.HoloEverywhereHelper;
import org.androidannotations.helper.ViewNotifierHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.*;

public class EFragmentHolder extends EComponentHolder implements HasViewChanged {

	private ViewNotifierHelper viewNotifierHelper;
	private JFieldVar contentView;
	private JBlock setContentViewBlock;
	private JVar inflater;
	private JVar container;
	private JDefinedClass fragmentBuilderClass;
	private JFieldVar fragmentArgumentsBuilderField;
	private ViewChangedHolder viewChangedHolder;
	private JMethod findNativeFragmentById;
	private JMethod findSupportFragmentById;

	public EFragmentHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		viewNotifierHelper = new ViewNotifierHelper(this);
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

	@Override
	public ViewChangedHolder getOnViewChangedHolder() {
		if (viewChangedHolder == null) {
			setViewChangedHolder();
		}
		return viewChangedHolder;
	}

	private void setViewChangedHolder() {
		viewChangedHolder = ViewChangedHolder.createViewChangedHolder(this);
	}

	@Override
	public JMethod getFindNativeFragmentById() {
		if (findNativeFragmentById == null) {
			setFindNativeFragmentById();
		}
		return findNativeFragmentById;
	}

	private void setFindNativeFragmentById() {
		findNativeFragmentById = FindFragmentHelper.createFindNativeFragmentById(this);
	}

	@Override
	public JMethod getFindSupportFragmentById() {
		if (findSupportFragmentById == null) {
			setFindSupportFragmentById();
		}
		return findSupportFragmentById;
	}

	private void setFindSupportFragmentById() {
		findSupportFragmentById = FindFragmentHelper.createFindSupportFragmentById(this);
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


}
