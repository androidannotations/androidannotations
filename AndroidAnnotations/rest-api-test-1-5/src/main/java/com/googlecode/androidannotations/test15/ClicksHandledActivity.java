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
