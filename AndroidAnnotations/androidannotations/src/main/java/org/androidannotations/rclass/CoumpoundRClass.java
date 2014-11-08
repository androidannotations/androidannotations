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
package org.androidannotations.rclass;

import java.util.HashMap;
import java.util.Map;

public class CoumpoundRClass implements IRClass {

	private final Map<String, IRInnerClass> rInnerClasses = new HashMap<>();

	public CoumpoundRClass(IRClass rClass, IRClass androidRclass) {
		for (Res res : Res.values()) {
			IRInnerClass rInnerClass = rClass.get(res);
			IRInnerClass androidRInnerClass = androidRclass.get(res);
			IRInnerClass coumpoundInnerClass = new CoumpoundInnerClass(rInnerClass, androidRInnerClass);
			rInnerClasses.put(res.rName(), coumpoundInnerClass);
		}
	}

	@Override
	public IRInnerClass get(Res res) {
		String id = res.rName();
		return rInnerClasses.get(id);
	}

}
