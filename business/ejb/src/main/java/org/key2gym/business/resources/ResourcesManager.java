/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
package org.key2gym.business.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

/**
 * 
 * @author Danylo Vashchilenko
 */
public class ResourcesManager {

	static {
		String language = System.getProperty("locale.language");
		String country = System.getProperty("locale.country");

		if (language != null && country != null) {
			/* If the locale was specified, uses it. */
			Locale locale = new Locale(language, country);

			strings = ResourceBundle.getBundle(
					"org/key2gym/business/resources/Strings", locale);

			Locale.setDefault(locale);
		} else {
			strings = ResourceBundle
					.getBundle("org/key2gym/business/resources/Strings");
		}

	}

	public static ResourceBundle getStrings() {
		return strings;
	}

	public static String getString(String key, Object... arguments) {
		String result = strings.getString(key);

		result = MessageFormat.format(result, arguments);

		return result;
	}

	private static ResourceBundle strings;
}
