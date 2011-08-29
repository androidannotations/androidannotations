/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.test15.res;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.view.animation.Animation;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.main)
public class ResActivity extends Activity {

    @AnimationRes(R.anim.fadein)
    XmlResourceParser xmlResAnim;
    
    @AnimationRes
    Animation fadein;
}
