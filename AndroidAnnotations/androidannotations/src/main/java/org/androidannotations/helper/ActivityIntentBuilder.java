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
package org.androidannotations.helper;

import static com.sun.codemodel.JMod.PUBLIC;

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ActivityIntentBuilder extends IntentBuilder {

	public ActivityIntentBuilder(HasIntentBuilder holder) {
		super(holder);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();
		createStart();
		createStartForResult();
	}

	private void createStart() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "start");
		method.body().invoke(contextField, "startActivity").arg(holder.getIntentField());
	}

	private void createStartForResult() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "startForResult");
		JVar requestCode = method.param(holder.codeModel().INT, "requestCode");

		JBlock body = method.body();
		JClass activityClass = holder.classes().ACTIVITY;
        JConditional condition = null;
        if (fragmentSupportField != null) {
            condition = body._if(fragmentSupportField.ne(JExpr._null()));
            condition._then() //
                    .invoke(fragmentSupportField, "startActivityForResult").arg(holder.getIntentField()).arg(requestCode);
        }
        if (fragmentField != null) {
            if (condition == null) {
                condition = body._if(fragmentField.ne(JExpr._null()));
            } else {
                condition = condition._elseif(fragmentField.ne(JExpr._null()));
            }
            condition._then() //
                    .invoke(fragmentField, "startActivityForResult").arg(holder.getIntentField()).arg(requestCode);
        }
        if (condition == null) {
            condition = body._if(contextField._instanceof(activityClass));
        } else {
            condition = condition._elseif(contextField._instanceof(activityClass));
        }
        condition._then() //
                .invoke(JExpr.cast(activityClass, contextField), "startActivityForResult").arg(holder.getIntentField()).arg(requestCode);
        condition._else() //
                .invoke(contextField, "startActivity").arg(holder.getIntentField());
	}
}
