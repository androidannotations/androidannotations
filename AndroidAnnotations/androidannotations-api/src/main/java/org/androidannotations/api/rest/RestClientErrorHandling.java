package org.androidannotations.api.rest;

/**
 * A @Rest interface implementing this interface will automatically have the
 * implementations of this method generated.
 */
public interface RestClientErrorHandling {
	/**
	 * Sets the error handler called when a rest error occurs.
	 * 
	 * @param handler
	 */
	void setRestErrorHandler(RestErrorHandler handler);
}
