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
package org.key2gym.client.resources;

import java.util.*;
import java.text.*;
import org.key2gym.client.Main;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ResourcesManager {

    static {
	strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
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
