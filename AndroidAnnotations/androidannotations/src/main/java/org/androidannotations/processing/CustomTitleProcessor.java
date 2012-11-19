/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import org.androidannotations.annotations.CustomTitle;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

import static com.sun.codemodel.JExpr.lit;


public class CustomTitleProcessor implements DecoratingElementProcessor {

    @Override
    public Class<? extends Annotation> getTarget() {
        return CustomTitle.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
        CustomTitle annotation = element.getAnnotation(CustomTitle.class);
        JFieldRef customTitleFeature = holder.classes().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");

        holder.init.body().invoke("requestWindowFeature").arg(customTitleFeature);
        holder.afterSetContentView.body().add(holder.contextRef.invoke("getWindow").invoke("setFeatureInt").arg(customTitleFeature).arg(lit(annotation.value())));
    }
}