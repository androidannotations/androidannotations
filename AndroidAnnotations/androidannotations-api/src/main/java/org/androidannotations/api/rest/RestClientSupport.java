package org.androidannotations.api.rest;

import org.springframework.web.client.RestTemplate;

/**
 * A @Rest interface implementing this interface will automatically have the
 * implementations of these methods generated.
 */
public interface RestClientSupport {
	/**
	 * Gets the rest template used by the rest service implementation.
	 * 
	 * @return RestTemplate
	 */
	RestTemplate getRestTemplate();

	/**
	 * Sets the rest template used by the rest service implementation.
	 * 
	 * @param rt
	 */
	void setRestTemplate(RestTemplate rt);
}
