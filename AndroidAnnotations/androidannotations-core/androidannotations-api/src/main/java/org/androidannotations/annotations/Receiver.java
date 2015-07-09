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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation is intended to be used on methods of an {@link EActivity},
 * {@link EFragment} or {@link EService}. When this annotation is used, a
 * {@link android.content.BroadcastReceiver} will be created to receive the
 * Intent corresponding to the given actions and it will call the annotated
 * method.
 * </p>
 * <p>
 * The annotated method MUST return void and MAY have several parameters:
 * </p>
 * <ul>
 * <li>A {@link android.content.Context} which will be the context given in
 * {@code void onReceive(Context context, Intent intent)}</li>
 * <li>An {@link android.content.Intent}</li>
 * <li>Any native, {@link android.os.Parcelable} or {@link java.io.Serializable}
 * parameters annotated with {@link Receiver.Extra} which will be the extra put
 * in the intent. The key of this extra is the value of the annotation
 * {@link Receiver.Extra} if set or the name of the parameter.</li>
 * </ul>
 * <p>
 * The annotation has four parameters:
 * </p>
 * <ul>
 * <li>
 * {@link #actions()}: One or several {@link java.lang.String} indicating the
 * actions which will spark the method. This parameter is MANDATORY</li>
 * <li>
 * {@link #dataSchemes()}: One or several {@link java.lang.String} indicating
 * the data schemes which should be handled.</li>
 * <li>
 * {@link #registerAt()}: The moment when the
 * {@link android.content.BroadcastReceiver} will be registered and
 * unregistered. By default : OnCreate/OnDestroy. The available values depend on
 * the enclosing enhanced component.</li>
 * <li>
 * {@link #local()}: Specify whether
 * android.support.v4.content.LocalBroadcastManager should be used. To use
 * android.support.v4.content.LocalBroadcastManager, you MUST have android
 * support-v4 in your classpath. Default value is false.</li>
 * </ul>
 *
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EActivity
 * public class MyActivity {
 * 
 *      &#064;Receiver(actions = {@link android.net.wifi.WifiManager#WIFI_STATE_CHANGED_ACTION})
 *      public void onWifiStateChanged(Intent intent);
 * 
 *      &#064;Receiver(actions = {@link android.net.wifi.WifiManager#WIFI_STATE_CHANGED_ACTION}, registerAt = RegisterAt.OnResumeOnPause)
 *      public void onWifiStateChangedWithoutIntent();
 *      
 *      &#064;Receiver(actions = {@link android.net.wifi.WifiManager#WIFI_STATE_CHANGED_ACTION})
 *      public void onWifiStateChangedWithInjectedExtra(@Receiver.Extra({@link android.net.wifi.WifiManager#EXTRA_WIFI_STATE}) int wifiState);
 *      
 *      &#064;Receiver(actions = {@link android.content.Intent#ACTION_VIEW}, dataSchemes = "http")
 *      public void onHttpUrlOpened(Intent intent);
 *      
 *      &#064;Receiver(actions = {@link android.content.Intent#ACTION_VIEW}, dataSchemes = {"http", "https"})
 *      public void onHttpOrHttpsUrlOpened(Intent intent);
 * 
 * }
 * </pre>
 *
 * </blockquote>
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Receiver {

	/**
	 * The strings indicating the actions which will spark the method.
	 * 
	 * @return the actions which will spark the method
	 */
	String[] actions();

	/**
	 * The strings indicating the data schemes which should be handled.
	 * 
	 * @return the data schemes which should be handled
	 */
	String[] dataSchemes() default {};

	/**
	 * The event pair when the receiver should be registered/unregistered.
	 * 
	 * @return the registration/unregistration point
	 */
	RegisterAt registerAt() default RegisterAt.OnCreateOnDestroy;

	/**
	 * Whether to use LocalBroadcastManager.
	 * 
	 * @return <b>true</b>, if LocalBroadcastManager should be used,
	 *         <b>false</b> otherwise
	 *
	 */
	boolean local() default false;

	/**
	 * Represents event pairs for BroadcastReceiver registration/unregistration.
	 */
	public enum RegisterAt {
		/**
		 * Register in the onCreate method, unregister in the onDestroy method.
		 */
		OnCreateOnDestroy, //
		/**
		 * Register in the onStart method, unregister in the onStop method.
		 */
		OnStartOnStop, //
		/**
		 * Register in the onResume method, unregister in the onPause method.
		 */
		OnResumeOnPause, //
		/**
		 * Register in the onAttach method, unregister in the onDetach method.
		 */
		OnAttachOnDetach
	}

	/**
	 * <p>
	 * Should be used on any native, {@link android.os.Parcelable} or
	 * {@link java.io.Serializable} parameter of a method annotated with
	 * {@link ReceiverAction} to inject the extra put in the intent parameter of
	 * {@code void onReceive(Context context, Intent intent)}. The key of this
	 * extra is the value of the annotation {@link ReceiverAction.Extra} if it
	 * is set or the name of the parameter.
	 * </p>
	 */
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.PARAMETER)
	public @interface Extra {

		/**
		 * Defines the extra's name. If this parameter isn't set the annotated
		 * parameter name will be used.
		 * 
		 * @return the name of the extra
		 */
		String value() default "";
	}
}
