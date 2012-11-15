/*
 * Copyright 2012 Danylo Vashchilenko
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
package org.key2gym.client.resources;

import java.util.Locale;
import java.util.ResourceBundle;
import org.key2gym.client.Main;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ResourcesManager {

    public static ResourceBundle getStrings() {
        if (strings == null) {
            Locale locale = new Locale(Main.getProperties().getProperty(Main.PROPERTY_LOCALE_LANGUAGE),
                    Main.getProperties().getProperty(Main.PROPERTY_LOCALE_COUNTRY));
            strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings", locale);
        }

        return strings;
    }
    
    private static ResourceBundle strings;
}
