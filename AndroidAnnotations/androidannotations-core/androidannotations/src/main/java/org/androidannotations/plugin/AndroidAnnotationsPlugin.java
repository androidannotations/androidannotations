/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.plugin;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.internal.exception.VersionNotFoundException;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;

public abstract class AndroidAnnotationsPlugin {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String version;
	private String apiVersion;

	public abstract String getName();

	public abstract List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv);

	@Override
	public String toString() {
		return getName();
	}

	public List<Option> getSupportedOptions() {
		return Collections.emptyList();
	}

	public boolean shouldCheckApiAndProcessorVersions() {
		return true;
	}

	public final void loadVersion() throws FileNotFoundException, VersionNotFoundException {
		version = getVersionFromPropertyFile(getName().toLowerCase());
		apiVersion = getVersionFromPropertyFile(getName().toLowerCase() + "-api");
	}

	private String getVersionFromPropertyFile(String name) throws FileNotFoundException, VersionNotFoundException {
		String filename = name + ".properties";
		Properties properties;
		try {
			URL url = getClass().getClassLoader().getResource(filename);
			properties = new Properties();
			properties.load(url.openStream());
		} catch (Exception e) {
			logger.error("Property file {} couldn't be parsed", filename);
			throw new FileNotFoundException("Property file " + filename + " couldn't be parsed.");
		}

		String version = properties.getProperty("version");

		if (version == null) {
			logger.error("{} plugin is missing 'version' property!", getName());
			throw new VersionNotFoundException(this);
		}

		return version;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getVersion() {
		return version;
	}
}
