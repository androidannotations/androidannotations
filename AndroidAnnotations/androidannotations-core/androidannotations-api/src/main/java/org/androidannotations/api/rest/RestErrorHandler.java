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
package org.androidannotations.api.rest;

import org.springframework.core.NestedRuntimeException;

/**
 * This interface is used for handling rest client exceptions.
 */
public interface RestErrorHandler {
	/**
	 * Called when an exception occurs within the rest client.
	 * 
	 * @param e Exception thrown by the Rest service
	 */
	void onRestClientExceptionThrown(NestedRuntimeException e);
}
