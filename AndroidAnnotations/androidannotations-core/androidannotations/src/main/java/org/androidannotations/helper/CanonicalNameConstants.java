/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
	public static final String BYTE = Byte.class.getCanonicalName();
	public static final String SHORT = Short.class.getCanonicalName();
	public static final String CHAR = Character.class.getCanonicalName();
	public static final String DOUBLE = Double.class.getCanonicalName();

	/*
	 * Android
	 */
	public static final String LOG = "android.util.Log";
	public static final String PARCELABLE = "android.os.Parcelable";
	public static final String INTENT = "android.content.Intent";
	public static final String INTENT_FILTER = "android.content.IntentFilter";
	public static final String COMPONENT_NAME = "android.content.ComponentName";
	public static final String BUNDLE = "android.os.Bundle";
	public static final String IBINDER = "android.os.IBinder";
	public static final String SPARSE_ARRAY = "android.util.SparseArray";
	public static final String SPARSE_BOOLEAN_ARRAY = "android.util.SparseBooleanArray";
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
	public static final String RADIO_GROUP = "android.widget.RadioGroup";
	public static final String RADIO_GROUP_ON_CHECKED_CHANGE_LISTENER = "android.widget.RadioGroup.OnCheckedChangeListener";
	public static final String VIEW = "android.view.View";
	public static final String VIEW_ON_CLICK_LISTENER = "android.view.View.OnClickListener";
	public static final String VIEW_ON_TOUCH_LISTENER = "android.view.View.OnTouchListener";
	public static final String VIEW_ON_LONG_CLICK_LISTENER = "android.view.View.OnLongClickListener";
	public static final String VIEW_ON_FOCUS_CHANGE_LISTENER = "android.view.View.OnFocusChangeListener";
	public static final String VIEW_GROUP_LAYOUT_PARAMS = "android.view.ViewGroup.LayoutParams";
	public static final String VIEW_GROUP = "android.view.ViewGroup";
	public static final String CONTEXT = "android.content.Context";
	public static final String KEY_EVENT = "android.view.KeyEvent";
	public static final String KEY_EVENT_CALLBACK = "android.view.KeyEvent.Callback";
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
	public static final String SQLITE_OPEN_HELPER = "android.database.sqlite.SQLiteOpenHelper";
	public static final String VIEW_SERVER = "org.androidannotations.api.ViewServer";
	public static final String LOOPER = "android.os.Looper";
	public static final String POWER_MANAGER = "android.os.PowerManager";
	public static final String WAKE_LOCK = "android.os.PowerManager.WakeLock";
	public static final String BUILD_VERSION = "android.os.Build.VERSION";
	public static final String BUILD_VERSION_CODES = "android.os.Build.VERSION_CODES";
	public static final String PREFERENCE_ACTIVITY = "android.preference.PreferenceActivity";
	public static final String PREFERENCE_FRAGMENT = "android.preference.PreferenceFragment";
	public static final String SUPPORT_V4_PREFERENCE_FRAGMENT = "android.support.v4.preference.PreferenceFragment";
	public static final String SUPPORT_V7_PREFERENCE_FRAGMENTCOMPAT = "android.support.v7.preference.PreferenceFragmentCompat";
	public static final String SUPPORT_V14_PREFERENCE_FRAGMENT = "android.support.v14.preference.PreferenceFragment";
	public static final String MACHINARIUS_V4_PREFERENCE_FRAGMENT = "com.github.machinarius.preferencefragment.PreferenceFragment";
	public static final String ACTIVITY_COMPAT = "android.support.v4.app.ActivityCompat";
	public static final String CONTEXT_COMPAT = "android.support.v4.content.ContextCompat";
	public static final String PREFERENCE = "android.preference.Preference";
	public static final String SUPPORT_V7_PREFERENCE = "android.support.v7.preference.Preference";
	public static final String PREFERENCE_CHANGE_LISTENER = "android.preference.Preference.OnPreferenceChangeListener";
	public static final String SUPPORT_V7_PREFERENCE_CHANGE_LISTENER = "android.support.v7.preference.Preference.OnPreferenceChangeListener";
	public static final String PREFERENCE_CLICK_LISTENER = "android.preference.Preference.OnPreferenceClickListener";
	public static final String SUPPORT_V7_PREFERENCE_CLICK_LISTENER = "android.support.v7.preference.Preference.OnPreferenceClickListener";
	public static final String PREFERENCE_ACTIVITY_HEADER = "android.preference.PreferenceActivity.Header";
	public static final String APP_WIDGET_MANAGER = "android.appwidget.AppWidgetManager";
	public static final String WIFI_MANAGER = "android.net.wifi.WifiManager";
	public static final String AUDIO_MANAGER = "android.media.AudioManager";
	public static final String ACTIONBAR_ACTIVITY = "android.support.v7.app.ActionBarActivity";
	public static final String APPCOMPAT_ACTIVITY = "android.support.v7.app.AppCompatActivity";
	public static final String VIEW_PAGER = "android.support.v4.view.ViewPager";
	public static final String PAGE_CHANGE_LISTENER = "android.support.v4.view.ViewPager.OnPageChangeListener";

	/*
	 * Android permission
	 */
	public static final String INTERNET_PERMISSION = "android.permission.INTERNET";
	public static final String WAKELOCK_PERMISSION = "android.permission.WAKE_LOCK";

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
	 * Parceler
	 */
	public static final String PARCEL_ANNOTATION = "org.parceler.Parcel";
	public static final String PARCELS_UTILITY_CLASS = "org.parceler.Parcels";

	private CanonicalNameConstants() {
	}

}
