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
package org.androidannotations.api.sharedpreferences;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public final class SetXmlSerializer {

	private static final String NAMESPACE = "";
	private static final String STRING_TAG = "AA_string";
	private static final String SET_TAG = "AA_set";

	private SetXmlSerializer() {

	}

	public static String serialize(Set<String> set) {
		if (set == null) {
			set = Collections.emptySet();
		}

		StringWriter writer = new StringWriter();
		XmlSerializer serializer = Xml.newSerializer();

		try {
			serializer.setOutput(writer);
			serializer.startTag(NAMESPACE, SET_TAG);

			for (String string : set) {
				serializer.startTag(NAMESPACE, STRING_TAG) //
						.text(string) //
						.endTag(NAMESPACE, STRING_TAG);
			}

			serializer.endTag(NAMESPACE, SET_TAG) //
					.endDocument();

		} catch (IllegalArgumentException | IOException | IllegalStateException e) {
			// should never happen
		}

		return writer.toString();
	}

	public static Set<String> deserialize(String data) {
		Set<String> stringSet = new TreeSet<>();
		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(new StringReader(data));
			parser.next();
			parser.require(XmlPullParser.START_TAG, NAMESPACE, SET_TAG);

			while (parser.next() != XmlPullParser.END_TAG) {
				parser.require(XmlPullParser.START_TAG, NAMESPACE, STRING_TAG);

				parser.next();
				parser.require(XmlPullParser.TEXT, null, null);
				stringSet.add(parser.getText());

				parser.next();
				parser.require(XmlPullParser.END_TAG, null, STRING_TAG);
			}
		} catch (XmlPullParserException e) {
			Log.w("getStringSet", e);
			return null;
		} catch (IOException e) {
			Log.w("getStringSet", e);
			return null;
		}

		return stringSet;
	}
}
