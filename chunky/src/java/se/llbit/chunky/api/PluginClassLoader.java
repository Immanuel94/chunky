/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.api;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class loader used to isolate plugin classes from each other, in case plugins
 * use the same class names. This should prevent errors caused by separate
 * plugins using different versions of a common library.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PluginClassLoader {
	private final ClassLoader classLoader;

	/**
	 * Create a new class loader that will load classes from the Jar file with the
	 * given path.
	 * @param jarFile path to Jar file use to load classes.
	 */
	public PluginClassLoader(URL jarFile) {

		classLoader = new URLClassLoader(new URL[] { jarFile });
	}

	public Class<?> loadClass(String mainClass) throws ClassNotFoundException {
		return classLoader.loadClass(mainClass);
	}

}
