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
package com.googlecode.androidannotations.test15;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.view.View;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity(R.layout.clicks_handled)
public class ClicksHandledActivity extends Activity{
	
	View viewArgument;
	
	boolean conventionButtonClicked;
	boolean extendedConventionButtonClicked;
	boolean overridenConventionButtonClicked;
	boolean unboundButtonClicked;

	@Click
	public void conventionButton() {
		conventionButtonClicked = true;
	}

	@Click
	public void extendedConventionButtonClicked() {
		extendedConventionButtonClicked = true;
		/*
		 * Useless code, used to understand the Spring Android API.
		 */
		RestTemplate restTemplate = new RestTemplate(null);
		restTemplate.getForObject("", int.class);
		Map<String, Object> urlVariables = null;
		ResponseEntity<Object> forEntity = restTemplate.getForEntity("", Object.class, urlVariables);
		restTemplate.getForObject("", Object.class, urlVariables);
		ResponseEntity<Object> postForEntity = restTemplate.postForEntity("", null,  Object.class, urlVariables);
		Object postForObject = restTemplate.postForObject("", null, Object.class, urlVariables);
		Set<HttpMethod> optionsForAllow = restTemplate.optionsForAllow("", urlVariables);
		restTemplate.delete("", urlVariables);
		restTemplate.headForHeaders("", urlVariables);
	}
	
	@Click(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonClicked = true;
	}
	
	public void unboundButton() {
		unboundButtonClicked = true;
	}
	
	@Click
	public void buttonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
	}
	
}
