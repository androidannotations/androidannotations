package org.androidannotations.api.rest;

/**
 * A @Rest interface implementing this interface will automatically have the
 * implementations of these methods generated.
 */
public interface RestClientRootUrl {
	/**
	 * Gets the root URL for the rest service.
	 * 
	 * @return String
	 */
	String getRootUrl();

	/**
	 * Sets the root URL for the rest service.
	 * 
	 * @param rootUrl
	 */
	void setRootUrl(String rootUrl);

}
