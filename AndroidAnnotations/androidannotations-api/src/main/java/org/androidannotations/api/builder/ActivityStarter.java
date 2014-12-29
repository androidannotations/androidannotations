package org.androidannotations.api.builder;

/**
 * Provides methods for starting an {@link android.app.Activity Activity}.
 */
public interface ActivityStarter {

	/**
	 * Starts the {@link android.app.Activity Activity}, by calling
	 * {@link android.app.Activity#startActivity(android.content.Intent)
	 * Activity#startActivity(android.content.Intent)} for the previously given
	 * {@link android.content.Context Context} or Fragment or support Fragment
	 * objects. It also passes the given extras, the options
	 * {@link android.os.Bundle Bundle}, if new methods are available which
	 * accept that.
	 */
	void start();

	/**
	 * Starts the {@link android.app.Activity Activity} for result, by calling
	 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)
	 * Activity#startActivityForResult(android.content.Intent, int)} for the
	 * previously given {@link android.content.Context Context} or Fragment or
	 * support Fragment objects. It also passes the given extras, the options
	 * {@link android.os.Bundle Bundle}, if new methods are available which
	 * accept that.
	 * 
	 * @param requestCode
	 *            this code will be returned in onActivityResult() when the
	 *            activity exits.
	 */
	void startForResult(int requestCode);
}
