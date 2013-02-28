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
package org.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.rclass.IRClass;
import com.sun.codemodel.JMethod;

/**
 */
public class SeekBarTouchStopProcessor extends AbstractTrackingTouchProcessor {

	public SeekBarTouchStopProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv, rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return SeekBarTouchStop.class;
	}

	@Override
	protected JMethod getMethodToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder) {
		JMethod methodToCall = onSeekBarChangeListenerHolder.onStopTrackingTouchMethod;
		return methodToCall;
	}

}
