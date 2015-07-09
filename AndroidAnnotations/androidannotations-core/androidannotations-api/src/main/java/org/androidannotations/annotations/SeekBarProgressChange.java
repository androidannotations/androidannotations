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
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)}
 * when the progress level of a SeekBar has changed.
 * </p>
 * <p>
 * The annotation value should be one or several R.id.* fields that refers to an
 * android.widget.SeekBar. If not set, the method name will be used as the
 * R.id.* field name.
 * </p>
 * <p>
 * The method MAY have multiple parameter :
 * </p>
 * <ul>
 * <li>A {@link android.widget.SeekBar} parameter to determine which view has
 * targeted this event</li>
 * <li>An <code>int</code> parameter to get the progress level of the SeekBar</li>
 * <li>A <code>boolean</code> parameter to determine if this event was triggered
 * by the user</li>
 * </ul>
 * <p>
 * All these parameters are optional. Parameter names do not matter.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;ProgressChange(<b>R.id.seekBar</b>)
 * void onProgressChangeOnSeekBar(SeekBar seekBar, int progress, boolean fromUser) {
 * 	// Something Here
 * }
 * 
 * &#064;ProgressChange(<b>R.id.seekBar</b>)
 * void onProgressChangeOnSeekBar(SeekBar seekBar, int progress) {
 * 	// Something Here
 * }
 * 
 * &#064;ProgressChange(<b>{R.id.seekBar1, R.id.seekBar2}</b>)
 * void onProgressChangeOnSeekBar(SeekBar seekBar) {
 * 	// Something Here
 * }
 * 
 * &#064;ProgressChange(<b>{R.id.seekBar1, R.id.seekBar2}</b>)
 * void onProgressChangeOnSeekBar() {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @since 2.7
 * 
 * @see SeekBarTouchStart
 * @see SeekBarTouchStop
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SeekBarProgressChange {

	/**
	 * The R.id.* fields which refer to the SeekBars.
	 * 
	 * @return the ids of the SeekBars
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refer to the SeekBars.
	 * 
	 * @return the resource names of the SeekBars
	 */
	String[] resName() default "";

}
