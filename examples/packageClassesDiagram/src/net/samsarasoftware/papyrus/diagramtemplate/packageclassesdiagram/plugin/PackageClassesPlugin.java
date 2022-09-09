package net.samsarasoftware.papyrus.diagramtemplate.packageclassesdiagram.plugin;


/*-
 * #%L
 * net.samsarasoftware.scripting.ScriptingEngine
 * %%
 * Copyright (C) 2014 - 2020 Pere Joseph Rodriguez
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.EclipseUIPlugin;
import org.eclipse.emf.common.util.ResourceLocator;


public final class PackageClassesPlugin extends EMFPlugin {

	/**
	 * Keep track of the singleton.
	 *
	 */
	public static final PackageClassesPlugin INSTANCE = new PackageClassesPlugin();

	/**
	 * Keep track of the singleton.
	 *
	 */
	private static Implementation plugin;

	/**
	 * Create the instance.
	 *
	 */
	public PackageClassesPlugin() {
		super(new ResourceLocator[] {});
	}

	/**
	 * Returns the singleton instance of the Eclipse plugin.
	 * @return the singleton instance.
	 *
	 */
	@Override
	public ResourceLocator getPluginResourceLocator() {
		return plugin;
	}

	/**
	 * Returns the singleton instance of the Eclipse plugin.
	 * @return the singleton instance.
	 *
	 */
	public static Implementation getPlugin() {
		return plugin;
	}

	/**
	 * The actual implementation of the Eclipse <b>Plugin</b>.
	 *
	 */
	public static class Implementation extends EclipseUIPlugin {

		/**
		 * Creates an instance.
		 *
		 */
		public Implementation() {
			super();

			// Remember the static instance.
			//
			plugin = this;
		}
	}


}
