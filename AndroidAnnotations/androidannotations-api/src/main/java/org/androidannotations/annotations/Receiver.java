/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
 * This annotation is intended to be used on methods of an {@link EActivity}, {@link EFragment} or {@link EService}.
 * When this annotation is used, a {@link android.content.BroadcastReceiver}
 * will be created to receive the Intent corresponding to the given actions
 * and it will call the annotated method.
 * <p/>
 * The annotated method MUST return void and MAY have one parameter:
 * <ul>
 *     <li>An {@link android.content.Intent}</li>
 * </ul>
 * <p/>
 * The annotation has two parameters:
 * <ul>
 *     <li>
 *         {@link #actions()}: One or several {@link java.lang.String} indicating the actions which will spark the method.
 *         This parameter is MANDATORY
 *     </li>
 *     <li>
 *         {@link #registerAt()}: The moment when the {@link android.content.BroadcastReceiver}
 *         will be registered and unregistered. By default : OnCreate/OnDestroy.
 *         The available values depend on the enclosing enhanced component.
 *
 *     </li>
 * </ul>
 * <p/>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EActivity
 * public class MyActivity {
 *
 *      &#064;Receiver(actions={{@link android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION}}
 *      public void onWifiStateChanged(Intent intent);
 *
 *      &#064;Receiver(actions={{@link android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION}, registerAt=RegisterAt.OnResumeOnPause}
 *      public void onWifiStateChangedWithoutIntent();
 *
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Receiver {

	String[] actions();

	RegisterAt registerAt() default RegisterAt.OnCreateOnDestroy;

	public enum RegisterAt {
		OnCreateOnDestroy,
		OnStartOnStop,
		OnResumeOnPause,
		OnAttachOnDetach
	}
}
