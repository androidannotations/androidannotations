/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CanonicalNameConstants {

	/*
	 * Java
	 */
	public static final String OBJECT = Object.class.getCanonicalName();
	public static final String URI = URI.class.getCanonicalName();
	public static final String MAP = Map.class.getCanonicalName();
	public static final String SET = Set.class.getCanonicalName();
	public static final String LIST = List.class.getCanonicalName();
	public static final String COLLECTION = Collection.class.getCanonicalName();
	public static final String COLLECTIONS = Collections.class.getCanonicalName();
	public static final String STRING = String.class.getCanonicalName();
	public static final String STRING_BUILDER = StringBuilder.class.getCanonicalName();
	public static final String STRING_SET = "java.util.Set<java.lang.String>";
	public static final String CHAR_SEQUENCE = CharSequence.class.getCanonicalName();
	public static final String SQL_EXCEPTION = SQLException.class.getCanonicalName();
	public static final String INTEGER = Integer.class.getCanonicalName();
	public static final String BOOLEAN = Boolean.class.getCanonicalName();
	public static final String FLOAT = Float.class.getCanonicalName();
	public static final String LONG = Long.class.getCanonicalName();
	public static final String ARRAYLIST = ArrayList.class.getCanonicalName();
	public static final String SERIALIZABLE = Serializable.class.getCanonicalName();

	/*
	 * Android
	 */
	public static final String LOG = "android.util.Log";
	public static final String PARCELABLE = "android.os.Parcelable";
	public static final String INTENT = "android.content.Intent";
	public static final String INTENT_FILTER = "android.content.IntentFilter";
	public static final String COMPONENT_NAME = "android.content.ComponentName";
	public static final String BUNDLE = "android.os.Bundle";
	public static final String APPLICATION = "android.app.Application";
	public static final String ACTIVITY = "android.app.Activity";
	public static final String EDITABLE = "android.text.Editable";
	public static final String TEXT_WATCHER = "android.text.TextWatcher";
	public static final String SEEKBAR = "android.widget.SeekBar";
	public static final String ON_SEEKBAR_CHANGE_LISTENER = "android.widget.SeekBar.OnSeekBarChangeListener";
	public static final String TEXT_VIEW = "android.widget.TextView";
	public static final String TEXT_VIEW_ON_EDITOR_ACTION_LISTENER = "android.widget.TextView.OnEditorActionListener";
	public static final String COMPOUND_BUTTON = "android.widget.CompoundButton";
	public static final String COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER = "android.widget.CompoundButton.OnCheckedChangeListener";
	public static final String VIEW = "android.view.View";
	public static final String VIEW_ON_CLICK_LISTENER = "android.view.View.OnClickListener";
	public static final String VIEW_ON_TOUCH_LISTENER = "android.view.View.OnTouchListener";
	public static final String VIEW_ON_LONG_CLICK_LISTENER = "android.view.View.OnLongClickListener";
	public static final String VIEW_ON_FOCUS_CHANGE_LISTENER = "android.view.View.OnFocusChangeListener";
	public static final String VIEW_GROUP_LAYOUT_PARAMS = "android.view.ViewGroup.LayoutParams";
	public static final String VIEW_GROUP = "android.view.ViewGroup";
	public static final String CONTEXT = "android.content.Context";
	public static final String KEY_EVENT = "android.view.KeyEvent";
	public static final String LAYOUT_INFLATER = "android.view.LayoutInflater";
	public static final String FRAGMENT_ACTIVITY = "android.support.v4.app.FragmentActivity";
	public static final String FRAGMENT = "android.app.Fragment";
	public static final String SUPPORT_V4_FRAGMENT = "android.support.v4.app.Fragment";
	public static final String HTML = "android.text.Html";
	public static final String WINDOW_MANAGER_LAYOUT_PARAMS = "android.view.WindowManager.LayoutParams";
	public static final String ADAPTER_VIEW = "android.widget.AdapterView";
	public static final String ON_ITEM_CLICK_LISTENER = "android.widget.AdapterView.OnItemClickListener";
	public static final String ON_ITEM_LONG_CLICK_LISTENER = "android.widget.AdapterView.OnItemLongClickListener";
	public static final String ON_ITEM_SELECTED_LISTENER = "android.widget.AdapterView.OnItemSelectedListener";
	public static final String WINDOW = "android.view.Window";
	public static final String MENU_ITEM = "android.view.MenuItem";
	public static final String MENU_INFLATER = "android.view.MenuInflater";
	public static final String MENU = "android.view.Menu";
	public static final String ANIMATION = "android.view.animation.Animation";
	public static final String ANIMATION_UTILS = "android.view.animation.AnimationUtils";
	public static final String RESOURCES = "android.content.res.Resources";
	public static final String CONFIGURATION = "android.content.res.Configuration";
	public static final String MOTION_EVENT = "android.view.MotionEvent";
	public static final String HANDLER = "android.os.Handler";
	public static final String SERVICE = "android.app.Service";
	public static final String INTENT_SERVICE = "android.app.IntentService";
	public static final String BROADCAST_RECEIVER = "android.content.BroadcastReceiver";
	public static final String LOCAL_BROADCAST_MANAGER = "android.support.v4.content.LocalBroadcastManager";
	public static final String CONTENT_PROVIDER = "android.content.ContentProvider";
	public static final String SQLITE_DATABASE = "android.database.sqlite.SQLiteDatabase";
	public static final String KEY_STORE = "java.security.KeyStore";
	public static final String SQLLITE_OPEN_HELPER = "android.database.sqlite.SQLiteOpenHelper";
	public static final String VIEW_SERVER = "org.androidannotations.api.ViewServer";
	public static final String LOOPER = "android.os.Looper";
	public static final String POWER_MANAGER = "android.os.PowerManager";
	public static final String WAKE_LOCK = "android.os.PowerManager.WakeLock";
	public static final String BUILD_VERSION = "android.os.Build.VERSION";
	public static final String BUILD_VERSION_CODES = "android.os.Build.VERSION_CODES";
	public static final String PREFERENCE_ACTIVITY = "android.preference.PreferenceActivity";
	public static final String PREFERENCE_FRAGMENT = "android.preference.PreferenceFragment";
	public static final String SUPPORT_V4_PREFERENCE_FRAGMENT = "android.support.v4.preference.PreferenceFragment";
	public static final String MACHINARIUS_V4_PREFERENCE_FRAGMENT = "com.github.machinarius.preferencefragment.PreferenceFragment";
	public static final String ACTIVITY_COMPAT = "android.support.v4.app.ActivityCompat";
	public static final String CONTEXT_COMPAT = "android.support.v4.content.ContextCompat";
	public static final String PREFERENCE = "android.preference.Preference";
	public static final String PREFERENCE_CHANGE_LISTENER = "android.preference.Preference.OnPreferenceChangeListener";
	public static final String PREFERENCE_CLICK_LISTENER = "android.preference.Preference.OnPreferenceClickListener";
	public static final String PREFERENCE_ACTIVITY_HEADER = "android.preference.PreferenceActivity.Header";
	public static final String APP_WIDGET_MANAGER = "android.appwidget.AppWidgetManager";

	/*
	 * Android permission
	 */
	public static final String INTERNET_PERMISSION = "android.permission.INTERNET";
	public static final String WAKELOCK_PERMISSION = "android.permission.WAKE_LOCK";

	/*
	 * SpringFramework
	 */
	public static final String RESPONSE_ENTITY = "org.springframework.http.ResponseEntity";
	public static final String HTTP_HEADERS = "org.springframework.http.HttpHeaders";
	public static final String MEDIA_TYPE = "org.springframework.http.MediaType";
	public static final String HTTP_METHOD = "org.springframework.http.HttpMethod";
	public static final String HTTP_ENTITY = "org.springframework.http.HttpEntity";
	public static final String REST_TEMPLATE = "org.springframework.web.client.RestTemplate";
	public static final String HTTP_MESSAGE_CONVERTER = "org.springframework.http.converter.HttpMessageConverter";
	public static final String CLIENT_HTTP_REQUEST_INTERCEPTOR = "org.springframework.http.client.ClientHttpRequestInterceptor";
	public static final String CLIENT_HTTP_REQUEST_FACTORY = "org.springframework.http.client.ClientHttpRequestFactory";
	public static final String HTTP_AUTHENTICATION = "org.springframework.http.HttpAuthentication";
	public static final String HTTP_BASIC_AUTHENTICATION = "org.springframework.http.HttpBasicAuthentication";
	public static final String REST_CLIENT_EXCEPTION = "org.springframework.web.client.RestClientException";
	public static final String NESTED_RUNTIME_EXCEPTION = "org.springframework.core.NestedRuntimeException";

	/*
	 * RoboGuice
	 */
	public static final String ROBO_APPLICATION = "roboguice.application.RoboApplication";
	public static final String ON_START_EVENT_OLD = "roboguice.activity.event.OnStartEvent";
	public static final String ROBO_CONTEXT = "roboguice.util.RoboContext";
	public static final String ROBO_INJECTOR = "roboguice.inject.RoboInjector";
	public static final String KEY = "com.google.inject.Key";
	public static final String CONTENT_VIEW_LISTENER = "roboguice.inject.ContentViewListener";
	public static final String ON_RESTART_EVENT = "roboguice.activity.event.OnRestartEvent";
	public static final String ON_START_EVENT = "roboguice.context.event.OnStartEvent";
	public static final String ON_RESUME_EVENT = "roboguice.activity.event.OnResumeEvent";
	public static final String ON_PAUSE_EVENT = "roboguice.activity.event.OnPauseEvent";
	public static final String ON_NEW_INTENT_EVENT = "roboguice.activity.event.OnNewIntentEvent";
	public static final String EVENT_MANAGER = "roboguice.event.EventManager";
	public static final String CONTEXT_SCOPE = "roboguice.inject.ContextScope";
	public static final String VIEW_MEMBERS_INJECTOR = "roboguice.inject.ViewListener.ViewMembersInjector";
	public static final String ROBO_GUICE = "roboguice.RoboGuice";
	public static final String INJECT = "com.google.inject.Inject";
	public static final String ON_STOP_EVENT = "roboguice.activity.event.OnStopEvent";
	public static final String ON_DESTROY_EVENT = "roboguice.context.event.OnDestroyEvent";
	public static final String ON_CONFIGURATION_CHANGED_EVENT = "roboguice.context.event.OnConfigurationChangedEvent";
	public static final String ON_CONTENT_CHANGED_EVENT = "roboguice.activity.event.OnContentChangedEvent";
	public static final String ON_ACTIVITY_RESULT_EVENT = "roboguice.activity.event.OnActivityResultEvent";
	public static final String ON_CONTENT_VIEW_AVAILABLE_EVENT = "roboguice.activity.event.OnContentViewAvailableEvent";
	public static final String ON_CREATE_EVENT = "roboguice.context.event.OnCreateEvent";

	/*
	 * ORMLite
	 */
	public static final String DAO = "com.j256.ormlite.dao.Dao";
	public static final String DAO_MANAGER = "com.j256.ormlite.dao.DaoManager";
	public static final String CONNECTION_SOURCE = "com.j256.ormlite.support.ConnectionSource";
	public static final String OPEN_HELPER_MANAGER = "com.j256.ormlite.android.apptools.OpenHelperManager";
	public static final String RUNTIME_EXCEPTION_DAO = "com.j256.ormlite.dao.RuntimeExceptionDao";

	/*
	 * HttpClient
	 */
	public static final String CLIENT_CONNECTION_MANAGER = "org.apache.http.conn.ClientConnectionManager";
	public static final String DEFAULT_HTTP_CLIENT = "org.apache.http.impl.client.DefaultHttpClient";
	public static final String SSL_SOCKET_FACTORY = "org.apache.http.conn.ssl.SSLSocketFactory";
	public static final String PLAIN_SOCKET_FACTORY = "org.apache.http.conn.scheme.PlainSocketFactory";
	public static final String SCHEME = "org.apache.http.conn.scheme.Scheme";
	public static final String SCHEME_REGISTRY = "org.apache.http.conn.scheme.SchemeRegistry";
	public static final String SINGLE_CLIENT_CONN_MANAGER = "org.apache.http.impl.conn.SingleClientConnManager";

	/*
	 * Otto
	 */
	public static final String SUBSCRIBE = "com.squareup.otto.Subscribe";
	public static final String PRODUCE = "com.squareup.otto.Produce";

	private CanonicalNameConstants() {
	}

}
