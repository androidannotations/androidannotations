/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
 * <p>
 * This annotation is intended to be used on methods to run it into a database
 * transaction.
 * </p>
 * <p>
 * The method MUST have at least one parameter :
 * </p>
 * <ul>
 * <li>A {@link android.database.sqlite.SQLiteDatabase} parameter at **FIRST**
 * position</li>
 * </ul>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;Transactional
 * 	void successfulTransaction(SQLiteDatabase db) {
 * 		db.execSQL(&quot;Some SQL&quot;);
 * 	}
 * 
 * 	&#064;Transactional
 * 	void mehodUsingArrayParameters(SQLiteDatabase db, MySerializableBean[] parameters) {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see OrmLiteDao
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Transactional {
}
