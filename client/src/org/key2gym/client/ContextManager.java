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
package org.key2gym.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.resources.ResourcesManager;

/**
 * Manages the contexts used by the application.
 * <p/>
 * 
 * The class provides means to login and logout, lookup EJBs within the current
 * context.
 * <p/>
 * 
 * The manager supports one shadow context. For instance, there is a context that
 * the user opened. It's called the primary context. All lookups are done with the
 * primary context. If the user logs in one more time, the current primary context
 * becomes the shadow context, which is kept open but is not used for lookups. The 
 * context opened during the second login becomes the new primary context. Then,
 * if the user logs out, the primary context is closed and the shadow context becomes
 * the primary. As it was already mentioned the manager supports only one shadow
 * context, which means that the user log in at most 2 times without logging out.
 * 
 * @author Danylo Vashchilenko
 */
public class ContextManager extends Observable {

    static {
        instance = new ContextManager();
    }

    public void login(String username, String password) throws ValidationException {

        Properties properties = new Properties();

        properties.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.put("java.naming.provider.url", Main.getProperties().getProperty(Main.PROPERTY_CONNECTION_URL));
        properties.put("openejb.authentication.realmName", "key2gym");
        properties.put("java.naming.security.principal", username);
        properties.put("java.naming.security.credentials", hashWithSHA256(password));

        Logger.getLogger(ContextManager.class).debug("Log in attempt: " + username + " " + password);

        InitialContext newContext;
        try {
            newContext = new InitialContext(properties);
        } catch (AuthenticationException ex) {
            Logger.getLogger(ContextManager.class).info("Authentication failed for the user: " + username);
            throw new ValidationException(ResourcesManager.getStrings().getString("Message.LoggingInFailed"));
        } catch (NamingException ex) {
            throw new RuntimeException("Failed to create InitialContext!", ex);
        }

        if (context == null) {
            context = newContext;
        } else if (shadowContext == null) {
            shadowContext = context;
            context = newContext;
        } else {
            throw new RuntimeException("There is already a primary and a shadow contexts.");
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Hashes the password with SHA-256.
     * 
     * @password the password to hash
     */
    private String hashWithSHA256(String password) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256"); //NOI18N
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        try {
            md.update(String.valueOf(password).getBytes("UTF-8")); //NOI18N
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        byte byteData[] = md.digest();

        /*
         * Converts the bytes to a hex string
         */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public void logout() {
        try {
            context.close();

            if (shadowContext != null) {
                context = shadowContext;
                shadowContext = null;
            }
        } catch (NamingException ex) {
            Logger.getLogger(ContextManager.class).error("Failed to close the context!", ex);
        }

        setChanged();
        notifyObservers(context);
    }

    /**
     * Looks up the EJB using the current context.
     * 
     * @param <T> the type to lookup
     * @param clazz the class of the type to lookup
     * @return an instance of the EJB
     */
    public static <T> T lookup(Class<T> clazz) {
        try {
            Logger.getLogger(ContextManager.class).debug("Looking up: " + clazz.getSimpleName());
            return (T) context.lookup(clazz.getSimpleName());
        } catch (NamingException ex) {
            throw new RuntimeException("Failed to lookup an EJB: ", ex);
        }
    }

    public boolean isContextAvailable() {
        return context != null;
    }

    public boolean hasShadowContext() {
        return shadowContext != null;
    }

    public static ContextManager getInstance() {
        return instance;
    }
    private static ContextManager instance;
    /**
     * The primary context.
     * 
     * This context used for all lookups.
     */
    private static InitialContext context;
    /**
     * The shadow context. 
     * 
     * The user can open a second session
     * without closing the first one. The primary context becomes the shadow
     * context and the second context becomes the new primary context.
     */
    private static InitialContext shadowContext;
}
