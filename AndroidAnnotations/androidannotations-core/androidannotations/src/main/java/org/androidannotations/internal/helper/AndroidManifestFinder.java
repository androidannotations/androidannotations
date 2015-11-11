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
package org.androidannotations.internal.helper;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.util.Elements;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.internal.exception.AndroidManifestNotFoundException;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AndroidManifestFinder {

	public static final Option OPTION_MANIFEST = new Option("androidManifestFile", null);

	public static final Option OPTION_LIBRARY = new Option("library", "false");

	private static final Logger LOGGER = LoggerFactory.getLogger(AndroidManifestFinder.class);

	private final AndroidAnnotationsEnvironment environment;

	public AndroidManifestFinder(AndroidAnnotationsEnvironment environment) {
		this.environment = environment;
	}

	public AndroidManifest extractAndroidManifest() throws AndroidManifestNotFoundException {
		try {
			File androidManifestFile = findManifestFile();
			String projectDirectory = androidManifestFile.getParent();

			boolean libraryOption = environment.getOptionBooleanValue(OPTION_LIBRARY);

			if (libraryOption) {
				return parse(androidManifestFile, true);
			}

			File projectProperties = new File(projectDirectory, "project.properties");

			boolean libraryProject = false;
			if (projectProperties.exists()) {
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(projectProperties));
					if (properties.containsKey("android.library")) {
						String androidLibraryProperty = properties.getProperty("android.library");
						libraryProject = androidLibraryProperty.equals("true");

						LOGGER.debug("Found android.library={} property in project.properties", libraryProject);
					}
				} catch (IOException ignored) {
					// we assume the project is not a library
				}
			}

			return parse(androidManifestFile, libraryProject);
		} catch (FileNotFoundException exception) {
			throw new AndroidManifestNotFoundException("Unable to find AndroidManifest.xml", exception);
		}
	}

	private File findManifestFile() throws FileNotFoundException {
		String androidManifestFile = environment.getOptionValue(OPTION_MANIFEST);
		if (androidManifestFile != null) {
			return findManifestInSpecifiedPath(androidManifestFile);
		} else {
			return findManifestInKnownPaths();
		}
	}

	private File findManifestInSpecifiedPath(String androidManifestPath) throws FileNotFoundException {
		File androidManifestFile = new File(androidManifestPath);
		if (!androidManifestFile.exists()) {
			LOGGER.error("Could not find the AndroidManifest.xml file in specified path : {}", androidManifestPath);
			throw new FileNotFoundException();
		} else {
			LOGGER.debug("AndroidManifest.xml file found with specified path: {}", androidManifestFile.toString());
		}
		return androidManifestFile;
	}

	private File findManifestInKnownPaths() throws FileNotFoundException {
		FileHelper.FileHolder holder = FileHelper.findRootProjectHolder(environment.getProcessingEnvironment());
		return findManifestInKnownPathsStartingFromGenFolder(holder.sourcesGenerationFolder.getAbsolutePath());
	}

	File findManifestInKnownPathsStartingFromGenFolder(String sourcesGenerationFolder) throws FileNotFoundException {
		Iterable<AndroidManifestFinderStrategy> strategies = Arrays.asList(new GradleAndroidManifestFinderStrategy(sourcesGenerationFolder),
				new MavenAndroidManifestFinderStrategy(sourcesGenerationFolder), new EclipseAndroidManifestFinderStrategy(sourcesGenerationFolder));

		AndroidManifestFinderStrategy applyingStrategy = null;

		for (AndroidManifestFinderStrategy strategy : strategies) {
			if (strategy.applies()) {
				applyingStrategy = strategy;
				break;
			}
		}

		File androidManifestFile = null;

		if (applyingStrategy != null) {
			androidManifestFile = applyingStrategy.findAndroidManifestFile();
		}

		if (androidManifestFile != null) {
			LOGGER.debug("{} AndroidManifest.xml file found using generation folder {}: {}", applyingStrategy.name, sourcesGenerationFolder, androidManifestFile.toString());
		} else {
			LOGGER.error("Could not find the AndroidManifest.xml file, using  generation folder [{}])", sourcesGenerationFolder);
		}

		return androidManifestFile;
	}

	private static abstract class AndroidManifestFinderStrategy {
		final String name;

		final Matcher matcher;

		AndroidManifestFinderStrategy(String name, Pattern sourceFolderPattern, String sourceFolder) {
			this.name = name;
			this.matcher = sourceFolderPattern.matcher(sourceFolder);
		}

		File findAndroidManifestFile() {
			for (String location : possibleLocations()) {
				File manifestFile = new File(matcher.group(1), location + "/AndroidManifest.xml");
				if (manifestFile.exists()) {
					return manifestFile;
				}
			}
			return null;
		}

		boolean applies() {
			return  matcher.matches();
		}

		abstract Iterable<String> possibleLocations();
	}

	private static class GradleAndroidManifestFinderStrategy extends AndroidManifestFinderStrategy {

		static final Pattern GRADLE_GEN_FOLDER = Pattern.compile("^(.*?)build[\\\\/]generated[\\\\/]source[\\\\/]apt(.*)$");

		GradleAndroidManifestFinderStrategy(String sourceFolder) {
			super("Gradle", GRADLE_GEN_FOLDER, sourceFolder);
		}

		@Override
		Iterable<String> possibleLocations() {
			String gradleVariant = matcher.group(2);

			return Arrays.asList("build/intermediates/manifests/full" + gradleVariant, "build/bundles" + gradleVariant);
		}
	}

	private static class MavenAndroidManifestFinderStrategy extends AndroidManifestFinderStrategy {

		static final Pattern MAVEN_GEN_FOLDER = Pattern.compile("^(.*?)target[\\\\/]generated-sources.*$");
		
		MavenAndroidManifestFinderStrategy(String sourceFolder) {
			super("Maven", MAVEN_GEN_FOLDER, sourceFolder);
		}

		@Override
		Iterable<String> possibleLocations() {
			return Arrays.asList("target", "src/main", "");
		}
	}

	private static class EclipseAndroidManifestFinderStrategy extends AndroidManifestFinderStrategy {

		static final Pattern ECLIPSE_GEN_FOLDER = Pattern.compile("^(.*?)\\.apt_generated.*$");
		
		EclipseAndroidManifestFinderStrategy(String sourceFolder) {
			super("Eclipse", ECLIPSE_GEN_FOLDER, sourceFolder);
		}

		@Override
		Iterable<String> possibleLocations() {
			return Collections.singleton("");
		}
	}

	private AndroidManifest parse(File androidManifestFile, boolean libraryProject) throws AndroidManifestNotFoundException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

		Document doc;
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(androidManifestFile);
		} catch (Exception e) {
			LOGGER.error("Could not parse the AndroidManifest.xml file at path {}", androidManifestFile, e);
			throw new AndroidManifestNotFoundException("Could not parse the AndroidManifest.xml file at path {}" + androidManifestFile, e);
		}

		Element documentElement = doc.getDocumentElement();
		documentElement.normalize();

		String applicationPackage = documentElement.getAttribute("package");

		int minSdkVersion = -1;
		int maxSdkVersion = -1;
		int targetSdkVersion = -1;
		NodeList sdkNodes = documentElement.getElementsByTagName("uses-sdk");
		if (sdkNodes.getLength() > 0) {
			Node sdkNode = sdkNodes.item(0);
			minSdkVersion = extractAttributeIntValue(sdkNode, "android:minSdkVersion", -1);
			maxSdkVersion = extractAttributeIntValue(sdkNode, "android:maxSdkVersion", -1);
			targetSdkVersion = extractAttributeIntValue(sdkNode, "android:targetSdkVersion", -1);
		}

		if (libraryProject) {
			return AndroidManifest.createLibraryManifest(applicationPackage, minSdkVersion, maxSdkVersion, targetSdkVersion);
		}

		NodeList applicationNodes = documentElement.getElementsByTagName("application");

		String applicationClassQualifiedName = null;
		boolean applicationDebuggableMode = false;

		if (applicationNodes.getLength() > 0) {
			Node applicationNode = applicationNodes.item(0);
			Node nameAttribute = applicationNode.getAttributes().getNamedItem("android:name");

			applicationClassQualifiedName = manifestNameToValidQualifiedName(applicationPackage, nameAttribute);

			if (applicationClassQualifiedName == null) {
				if (nameAttribute != null) {
					LOGGER.warn("The class application declared in the AndroidManifest.xml cannot be found in the compile path: [{}]", nameAttribute.getNodeValue());
				}
			}

			Node debuggableAttribute = applicationNode.getAttributes().getNamedItem("android:debuggable");
			if (debuggableAttribute != null) {
				applicationDebuggableMode = debuggableAttribute.getNodeValue().equalsIgnoreCase("true");
			}
		}

		NodeList activityNodes = documentElement.getElementsByTagName("activity");
		List<String> activityQualifiedNames = extractComponentNames(applicationPackage, activityNodes);

		NodeList serviceNodes = documentElement.getElementsByTagName("service");
		List<String> serviceQualifiedNames = extractComponentNames(applicationPackage, serviceNodes);

		NodeList receiverNodes = documentElement.getElementsByTagName("receiver");
		List<String> receiverQualifiedNames = extractComponentNames(applicationPackage, receiverNodes);

		NodeList providerNodes = documentElement.getElementsByTagName("provider");
		List<String> providerQualifiedNames = extractComponentNames(applicationPackage, providerNodes);

		List<String> componentQualifiedNames = new ArrayList<>();
		componentQualifiedNames.addAll(activityQualifiedNames);
		componentQualifiedNames.addAll(serviceQualifiedNames);
		componentQualifiedNames.addAll(receiverQualifiedNames);
		componentQualifiedNames.addAll(providerQualifiedNames);

		NodeList usesPermissionNodes = documentElement.getElementsByTagName("uses-permission");
		List<String> usesPermissionQualifiedNames = extractUsesPermissionNames(usesPermissionNodes);

		List<String> permissionQualifiedNames = new ArrayList<>();
		permissionQualifiedNames.addAll(usesPermissionQualifiedNames);

		return AndroidManifest.createManifest(applicationPackage, applicationClassQualifiedName, componentQualifiedNames, permissionQualifiedNames,
				minSdkVersion, maxSdkVersion, targetSdkVersion, applicationDebuggableMode);
	}

	private int extractAttributeIntValue(Node node, String attribute, int defaultValue) {
		try {
			NamedNodeMap attributes = node.getAttributes();
			if (attributes.getLength() > 0) {
				Node attributeNode = attributes.getNamedItem(attribute);
				if (attributeNode != null) {
					return Integer.parseInt(attributeNode.getNodeValue());
				}
			}
		} catch (NumberFormatException ignored) {
			// we assume the manifest is well-formed
		}
		return defaultValue;
	}

	private List<String> extractComponentNames(String applicationPackage, NodeList componentNodes) {
		List<String> componentQualifiedNames = new ArrayList<>();

		for (int i = 0; i < componentNodes.getLength(); i++) {
			Node activityNode = componentNodes.item(i);
			Node nameAttribute = activityNode.getAttributes().getNamedItem("android:name");

			String qualifiedName = manifestNameToValidQualifiedName(applicationPackage, nameAttribute);

			if (qualifiedName != null) {
				componentQualifiedNames.add(qualifiedName);
			} else {
				if (nameAttribute != null) {
					LOGGER.warn("A class activity declared in the AndroidManifest.xml cannot be found in the compile path: [{}]", nameAttribute.getNodeValue());
				} else {
					LOGGER.warn("The {} activity node in the AndroidManifest.xml has no android:name attribute", i);
				}
			}
		}
		return componentQualifiedNames;
	}

	private String manifestNameToValidQualifiedName(String applicationPackage, Node nameAttribute) {
		if (nameAttribute != null) {
			String activityName = nameAttribute.getNodeValue();
			if (activityName.startsWith(applicationPackage)) {
				return returnClassIfExistsOrNull(activityName);
			} else {
				if (activityName.startsWith(".")) {
					return returnClassIfExistsOrNull(applicationPackage + activityName);
				} else {
					if (classOrModelClassExists(activityName)) {
						return activityName;
					} else {
						return returnClassIfExistsOrNull(applicationPackage + "." + activityName);
					}
				}
			}
		} else {
			return null;
		}
	}

	private boolean classOrModelClassExists(String className) {
		Elements elementUtils = environment.getProcessingEnvironment().getElementUtils();

		if (className.endsWith(classSuffix())) {
			className = className.substring(0, className.length() - classSuffix().length());
		}
		return elementUtils.getTypeElement(className) != null;
	}

	private String returnClassIfExistsOrNull(String className) {
		if (classOrModelClassExists(className)) {
			return className;
		} else {
			return null;
		}
	}

	private List<String> extractUsesPermissionNames(NodeList usesPermissionNodes) {
		List<String> usesPermissionQualifiedNames = new ArrayList<>();

		for (int i = 0; i < usesPermissionNodes.getLength(); i++) {
			Node usesPermissionNode = usesPermissionNodes.item(i);
			Node nameAttribute = usesPermissionNode.getAttributes().getNamedItem("android:name");

			if (nameAttribute == null) {
				return null;
			}

			usesPermissionQualifiedNames.add(nameAttribute.getNodeValue());
		}
		return usesPermissionQualifiedNames;
	}

}
