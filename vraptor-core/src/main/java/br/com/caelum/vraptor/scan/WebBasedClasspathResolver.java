/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.scan;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.reflections.util.ClasspathHelper;

import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * A classpath resolver based on ServletContext
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class WebBasedClasspathResolver extends AbstractClasspathResolver implements ClasspathResolver {

	private final ServletContext servletContext;

	public WebBasedClasspathResolver(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ClassLoader getClassLoader() {
		if (servletContext.getMajorVersion() == 3) {
			return servletContext.getClassLoader();
		}
		
		return Thread.currentThread().getContextClassLoader();
	}

	public URL findWebInfClassesLocation() {
		return ClasspathHelper.forWebInfClasses(servletContext);
	}
	
	public Set<URL> findWebInfLibLocations() {
		return ClasspathHelper.forWebInfLib(servletContext);
	}

	public List<String> findBasePackages() {
		ArrayList<String> packages = new ArrayList<String>();

		// find packages from web.xml
		String packagesParam = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
		if (packagesParam != null) {
			Collections.addAll(packages, packagesParam.trim().split("\\s*,\\s*"));
		}

		// find plugin packages
		getPackagesFromPluginsJARs(packages);
		
		return packages;
	}
}
