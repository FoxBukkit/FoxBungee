/**
 * This file is part of YiffBungee.
 *
 * YiffBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBungee.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbungee.main.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
public class Utils {
	public static String concat(Collection<String> parts, int start, String defaultText) {
		// TODO: optimize
		return concatArray(parts.toArray(new String[parts.size()]), start, defaultText);
	}

	public static String concatArray(String[] array, int start, String defaultText) {
		if (array.length <= start)
			return defaultText;

		if (array.length <= start + 1)
			return array[start]; // optimization

		StringBuilder ret = new StringBuilder(array[start]);
		for(int i = start + 1; i < array.length; i++) {
			ret.append(' ');
			ret.append(array[i]);
		}
		return ret.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E instance, String field) {
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(instance);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
		try
		{
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);

			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);

			if ((modifiers & Modifier.FINAL) != 0)
				field_modifiers.setInt(f, modifiers & ~Modifier.FINAL);

			f.setAccessible(true);
			f.set(instance, value);
		}
		catch (Exception e) {
			System.err.println("Could not set field \"" + field + "\" of class \"" + instanceclass.getCanonicalName() + "\" because \"" + e.getMessage() + "\"");
		}
	}

	public static Pattern compileWildcard(String wildcard) {
		final StringBuilder pattern = new StringBuilder("^");

		boolean first = true;
		for (String part : wildcard.split("\\*")) {
			if (!first)
				pattern.append(".*");

			first = false;
			if (part.isEmpty())
				continue;

			pattern.append(Pattern.quote(part));
		}

		pattern.append("$");
		
		return Pattern.compile(pattern.toString());
	}

	public static double fuzzySignum(final double a) {
		if (a > 0.0001)
			return 1;

		if (a < -0.0001)
			return -1;

		return 0;
	}

	public static <T> List<Class<? extends T>> getSubClasses(Class<T> baseClass, String packageName) {
		final List<Class<? extends T>> ret = new ArrayList<>();
		final File file;
		try {
			final ProtectionDomain protectionDomain = baseClass.getProtectionDomain();
			final CodeSource codeSource = protectionDomain.getCodeSource();
			if (codeSource == null)
				return ret;

			final URL location = codeSource.getLocation();
			final URI uri = location.toURI();
			file = new File(uri);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return ret;
		}
		final String[] fileList;

		if (file.isDirectory() || (file.isFile() && !file.getName().endsWith(".jar"))) {
			String packageFolderName = "/"+packageName.replace('.','/');

			URL url = baseClass.getResource(packageFolderName);
			if (url == null)
				return ret;

			File directory = new File(url.getFile());
			if (!directory.exists())
				return ret;

			// Get the list of the files contained in the package
			fileList = directory.list();
		}
		else if (file.isFile()) {
			final List<String> tmp = new ArrayList<>();
			final JarFile jarFile;
			try {
				jarFile = new JarFile(file);
			}
			catch (IOException e) {
				e.printStackTrace();
				return ret;
			}

			Pattern pathPattern = Pattern.compile(packageName.replace('.','/')+"/(.+\\.class)");
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				Matcher matcher = pathPattern.matcher(entries.nextElement().getName());
				if (!matcher.matches())
					continue;

				tmp.add(matcher.group(1));
			}

			fileList = tmp.toArray(new String[tmp.size()]);
		}
		else {
			return ret;
		}

		Pattern classFilePattern = Pattern.compile("(.+)\\.class");
		for (String fileName : fileList) {
			// we are only interested in .class files
			Matcher matcher = classFilePattern.matcher(fileName);
			if (!matcher.matches())
				continue;

			// removes the .class extension
			String classname = matcher.group(1);
			try {
				final String qualifiedName = packageName+"."+classname.replace('/', '.');
				final Class<?> classObject = Class.forName(qualifiedName);
				final Class<? extends T> classT = classObject.asSubclass(baseClass);

				// Try to create an instance of the object
				ret.add(classT);
			}
			catch (ClassCastException e) {
				//noinspection UnnecessaryContinue
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static String firstLetterToUppercase(String string) {
		return Character.toUpperCase(string.charAt(0))+string.substring(1);
	}

	public static int countSpaces(String phrase) {
		int spaces = 0;
		int pos = 0;
		while ((pos = phrase.indexOf(' ', pos)) != -1 ) {
			phrase = phrase.substring(pos+1);
			++spaces;
		}
		return spaces;
	}

	/**
	 * Enumerates strings in natural english.
	 *
	 * @param strings The strings to enumerate.
	 * @return the enumerated strings.
	 */
	public static StringBuilder enumerateStrings(final List<String> strings) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.size(); ++i) {
			final String distance = strings.get(i);
			//noinspection StatementWithEmptyBody
			if (i == 0) {
				// Do nothing
			}
			else if (i == strings.size()-1) {
				sb.append(" and ");
			}
			else {
				sb.append(", ");
			}
			sb.append(distance);
		}
		return sb;
	}
}
