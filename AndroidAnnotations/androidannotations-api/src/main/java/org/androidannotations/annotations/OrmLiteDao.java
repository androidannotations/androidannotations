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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use it on fields of any enhanced class to injects an OrmLite Dao, configured
 * with the provided mode and helper classes.
 * <p/>
 * All parameters are mandatory:
 * <ul>
 * <li><i>helper</i>: should hold the class of your database helper which should
 * extend {@link com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper}</li>
 * <li><i>model</i>: hould point to the model class that the DAO relates to. It
 * should match the type of the first generic parameter of your Dao.</li>
 * </ul>
 * <p/>
 * <b>Note:</b> The minimum version required of ORMLite is 4.21
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * 	// UserDao is a Dao&lt;User, Long&gt;
 * 	&#064;OrmLiteDao(helper = DatabaseHelper.class, model = User.class)
 * 	UserDao userDao;
 * 
 * 	&#064;OrmLiteDao(helper = DatabaseHelper.class, model = Car.class)
 * 	Dao&lt;Car, Long&gt; carDao;
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface OrmLiteDao {

	Class<?> helper();

	Class<?> model();

}
