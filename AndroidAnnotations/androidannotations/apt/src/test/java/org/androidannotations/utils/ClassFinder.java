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
package org.androidannotations.utils;

import static java.util.Collections.synchronizedList;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Based on http://code.google.com/p/acris/wiki/AnnotationProcessing_Testing
 */
public class ClassFinder {

	private Map<URL, String> classpathLocations = new HashMap<>();
	private Map<Class<?>, URL> results = new HashMap<>();
	private List<Throwable> errors = new ArrayList<>();

	public ClassFinder() {
		refreshLocations();
	}

	/**
	 * Rescan the classpath, caching all possible file locations.
	 */
	public final void refreshLocations() {
		synchronized (classpathLocations) {
			classpathLocations = getClasspathLocations();
		}
	}

	/**
	 * @param fqcn
	 *            Name of superclass/interface on which to search
	 */
	public final List<Class<?>> findClassesInPackage(String packageName) {
		synchronized (classpathLocations) {
			synchronized (results) {
				errors = new ArrayList<>();
				results = new TreeMap<>(CLASS_COMPARATOR);
				return findSubclasses(classpathLocations, packageName);
			}
		}
	}

	public final List<Throwable> getErrors() {
		return new ArrayList<>(errors);
	}

	/**
	 * The result of the last search is cached in this object, along with the
	 * URL that corresponds to each class returned. This method may be called to
	 * query the cache for the location at which the given class was found.
	 * <code>null</code> will be returned if the given class was not found
	 * during the last search, or if the result cache has been cleared.
	 */
	public final URL getLocationOf(Class<?> cls) {
		if (results != null) {
			return results.get(cls);
		} else {
			return null;
		}
	}

	/**
	 * Determine every URL location defined by the current classpath, and it's
	 * associated package name.
	 */
	public final Map<URL, String> getClasspathLocations() {
		Map<URL, String> map = new TreeMap<>(URL_COMPARATOR);
		File file = null;

		String pathSep = System.getProperty("path.separator");
		String classpath = System.getProperty("java.class.path");

		StringTokenizer st = new StringTokenizer(classpath, pathSep);
		while (st.hasMoreTokens()) {
			String path = st.nextToken();
			file = new File(path);
			include(null, file, map);
		}

		return map;
	}

	private final static FileFilter DIRECTORIES_ONLY = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.exists() && f.isDirectory();
		}
	};

	private final static Comparator<URL> URL_COMPARATOR = new Comparator<URL>() {
		@Override
		public int compare(URL u1, URL u2) {
			return String.valueOf(u1).compareTo(String.valueOf(u2));
		}
	};

	private final static Comparator<Class<?>> CLASS_COMPARATOR = new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> c1, Class<?> c2) {
			return String.valueOf(c1).compareTo(String.valueOf(c2));
		}
	};

	private void include(String name, File file, Map<URL, String> map) {
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			// could be a JAR file
			includeJar(file, map);
			return;
		}

		if (name == null) {
			name = "";
		} else {
			name += ".";
		}

		// add subpackages
		File[] dirs = file.listFiles(DIRECTORIES_ONLY);
		for (File dir : dirs) {
			try {
				// add the present package
				map.put(new URL("file://" + dir.getCanonicalPath()), name + dir.getName());
			} catch (IOException ioe) {
				return;
			}

			include(name + dir.getName(), dir, map);
		}
	}

	private void includeJar(File file, Map<URL, String> map) {
		if (file.isDirectory()) {
			return;
		}

		URL jarURL = null;
		JarFile jar = null;
		try {
			jarURL = new URL("file:/" + file.getCanonicalPath());
			jarURL = new URL("jar:" + jarURL.toExternalForm() + "!/");
			JarURLConnection conn = (JarURLConnection) jarURL.openConnection();
			jar = conn.getJarFile();
		} catch (Exception e) {
			// not a JAR or disk I/O error
			// either way, just skip
			return;
		}

		if (jar == null || jarURL == null) {
			return;
		}

		// include the jar's "default" package (i.e. jar's root)
		map.put(jarURL, "");

		Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = e.nextElement();

			if (entry.isDirectory()) {
				if (entry.getName().toUpperCase().equals("META-INF/")) {
					continue;
				}

				try {
					map.put(new URL(jarURL.toExternalForm() + entry.getName()), packageNameFor(entry));
				} catch (MalformedURLException murl) {
					// whacky entry?
					continue;
				}
			}
		}
	}

	private static String packageNameFor(JarEntry entry) {
		if (entry == null) {
			return "";
		}
		String s = entry.getName();
		if (s == null) {
			return "";
		}
		if (s.length() == 0) {
			return s;
		}
		if (s.startsWith("/")) {
			s = s.substring(1, s.length());
		}
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return s.replace('/', '.');
	}

	private List<Class<?>> findSubclasses(Map<URL, String> locations, String searchingPackageName) {
		List<Class<?>> v = synchronizedList(new ArrayList<Class<?>>());

		List<Class<?>> w = null;

		for (URL url : locations.keySet()) {
			// search just required packages
			String packageName = locations.get(url);
			if (packageName.startsWith(searchingPackageName)) {
				w = findSubclasses(url, packageName, searchingPackageName);
				if (w != null && w.size() > 0) {
					v.addAll(w);
				}
			}
		}

		return v;
	}

	private List<Class<?>> findSubclasses(URL location, String packageName, String searchingPackageName) {

		synchronized (results) {

			// hash guarantees unique names...
			Map<Class<?>, URL> thisResult = new TreeMap<>(CLASS_COMPARATOR);
			List<Class<?>> v = synchronizedList(new ArrayList<Class<?>>());
			// ...but return a list

			List<URL> knownLocations = new ArrayList<>();
			knownLocations.add(location);
			// TODO: add getResourceLocations() to this list

			// iterate matching package locations...
			for (URL url : knownLocations) {
				// Get a File object for the package
				File directory = new File(url.getFile());

				if (directory.exists()) {
					// Get the list of the files contained in the package
					String[] files = directory.list();
					for (String file : files) {
						// we are only interested in .class files
						if (file.endsWith(".class")) {
							// removes the .class extension
							String classname = file.substring(0, file.length() - 6);

							try {
								Class<?> c = Class.forName(packageName + "." + classname);
								if (packageName.startsWith(searchingPackageName)) {
									thisResult.put(c, url);
								}
							} catch (Exception ex) {
								errors.add(ex);
							}
						}
					}
				} else {
					try {
						// It does not work with the filesystem: we must
						// be in the case of a package contained in a jar file.
						JarURLConnection conn = (JarURLConnection) url.openConnection();
						// String starts = conn.getEntryName();
						JarFile jarFile = conn.getJarFile();

						Enumeration<JarEntry> e = jarFile.entries();
						while (e.hasMoreElements()) {
							JarEntry entry = e.nextElement();
							String entryname = entry.getName();

							if (!entry.isDirectory() && entryname.endsWith(".class")) {
								String classname = entryname.substring(0, entryname.length() - 6);
								if (classname.startsWith("/")) {
									classname = classname.substring(1);
								}
								classname = classname.replace('/', '.');

								try {
									// TODO: verify this block
									Class<?> c = Class.forName(classname);
									if (c.getPackage().getName().startsWith(searchingPackageName)) {
										thisResult.put(c, url);
									}
								} catch (ClassNotFoundException cnfex) {
									// that's strange since we're scanning
									// the same classpath the classloader's
									// using... oh, well
									errors.add(cnfex);
								} catch (NoClassDefFoundError ncdfe) {
									// dependency problem... class is
									// unusable anyway, so just ignore it
									errors.add(ncdfe);
								} catch (UnsatisfiedLinkError ule) {
									// another dependency problem... class is
									// unusable anyway, so just ignore it
									errors.add(ule);
								} catch (Exception exception) {
									// unexpected problem
									// System.err.println (ex);
									errors.add(exception);
								} catch (Error error) {
									// lots of things could go wrong
									// that we'll just ignore since
									// they're so rare...
									errors.add(error);
								}
							}
						}
					} catch (IOException ioex) {
						errors.add(ioex);
					}
				}
			} // while

			results.putAll(thisResult);

			for (Class<?> aClass : thisResult.keySet()) {
				v.add(aClass);
			}
			return v;

		} // synch results
	}

	public static void main(String[] args) {

		ClassFinder finder = null;
		List<Class<?>> v = null;
		List<Throwable> errors = null;

		if (args.length == 1) {
			finder = new ClassFinder();
			v = finder.findClassesInPackage(args[0]);
			errors = finder.getErrors();
		} else {
			System.out.println("Usage: java ClassFinder <package.name>");
			return;
		}

		System.out.println("RESULTS:");
		if (v != null && v.size() > 0) {
			for (Class<?> cls : v) {
				System.out.println(cls + " in " + (finder != null ? String.valueOf(finder.getLocationOf(cls)) : "?"));
			}

			if (errors != null && errors.size() > 0) {

				System.out.println("Errors:");

				for (Throwable error : errors) {
					error.printStackTrace();
				}
			}
		} else {
			System.out.println("No subclasses in package " + args[0] + " found.");
		}
	}
}
