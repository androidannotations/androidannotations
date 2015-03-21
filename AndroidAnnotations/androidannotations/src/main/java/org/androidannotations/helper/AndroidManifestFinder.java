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
package org.androidannotations.helper;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.androidannotations.helper.FileHelper.FileHolder;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AndroidManifestFinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(AndroidManifestFinder.class);
	private static final int MAX_PARENTS_FROM_SOURCE_FOLDER = 10;

	private final ProcessingEnvironment processingEnv;
	private final OptionsHelper optionsHelper;

	public AndroidManifestFinder(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		optionsHelper = new OptionsHelper(processingEnv);
	}

	public Option<AndroidManifest> extractAndroidManifest() {
		Option<File> androidManifestFileOption = findManifestFile();

		if (androidManifestFileOption.isAbsent()) {
			return Option.absent();
		}

		File androidManifestFile = androidManifestFileOption.get();

		String projectDirectory = androidManifestFile.getParent();

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
	}

	private Option<File> findManifestFile() {
		String androidManifestFile = optionsHelper.getAndroidManifestFile();
		if (androidManifestFile != null) {
			return findManifestInSpecifiedPath(androidManifestFile);
		} else {
			return findManifestInParentsDirectories();
		}
	}

	private Option<File> findManifestInSpecifiedPath(String androidManifestPath) {
		File androidManifestFile = new File(androidManifestPath);
		if (!androidManifestFile.exists()) {
			LOGGER.error("Could not find the AndroidManifest.xml file in specified path : {}", androidManifestPath);
			return Option.absent();
		} else {
			LOGGER.debug("AndroidManifest.xml file found with specified path: {}", androidManifestFile.toString());
		}
		return Option.of(androidManifestFile);
	}

	/**
	 * We use a dirty trick to find the AndroidManifest.xml file, since it's not
	 * available in the classpath. The idea is quite simple : create a fake
	 * class file, retrieve its URI, and start going up in parent folders to
	 * find the AndroidManifest.xml file. Any better solution will be
	 * appreciated.
	 */
	private Option<File> findManifestInParentsDirectories() {
		Option<FileHolder> projectRootHolderOption = FileHelper.findRootProjectHolder(processingEnv);
		if (projectRootHolderOption.isAbsent()) {
			return Option.absent();
		}

		FileHolder projectRootHolder = projectRootHolderOption.get();
		File projectRoot = projectRootHolder.projectRoot;

		File androidManifestFile = new File(projectRoot, "AndroidManifest.xml");
		for (int i = 0; i < MAX_PARENTS_FROM_SOURCE_FOLDER; i++) {
			if (androidManifestFile.exists()) {
				break;
			} else {
				if (projectRoot.getParentFile() != null) {
					projectRoot = projectRoot.getParentFile();
					androidManifestFile = new File(projectRoot, "AndroidManifest.xml");
				} else {
					break;
				}
			}
		}

		if (!androidManifestFile.exists()) {
			LOGGER.error("Could not find the AndroidManifest.xml file, going up from path [{}] found using dummy file [] (max atempts: {})",
					projectRootHolder.sourcesGenerationFolder.getAbsolutePath(), projectRootHolder.dummySourceFilePath, MAX_PARENTS_FROM_SOURCE_FOLDER);
			return Option.absent();
		} else {
			LOGGER.debug("AndroidManifest.xml file found in parent folder {}: {}", projectRoot.getAbsolutePath(), androidManifestFile.toString());
		}

		return Option.of(androidManifestFile);
	}

	private Option<AndroidManifest> parse(File androidManifestFile, boolean libraryProject) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

		Document doc;
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(androidManifestFile);
		} catch (Exception e) {
			LOGGER.error("Could not parse the AndroidManifest.xml file at path {}", androidManifestFile, e);
			return Option.absent();
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
			return Option.of(AndroidManifest.createLibraryManifest(applicationPackage, minSdkVersion, maxSdkVersion, targetSdkVersion));
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

		return Option.of(AndroidManifest.createManifest(applicationPackage, applicationClassQualifiedName, componentQualifiedNames, permissionQualifiedNames, minSdkVersion, maxSdkVersion,
				targetSdkVersion, applicationDebuggableMode));
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
		Elements elementUtils = processingEnv.getElementUtils();

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
