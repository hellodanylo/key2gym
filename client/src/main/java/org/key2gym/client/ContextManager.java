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
package org.key2gym.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.remote.AdministratorsServiceRemote;
import org.key2gym.client.resources.ResourcesManager;

import com.sun.appserv.security.ProgrammaticLogin;

/**
 * Manages the contexts used by the application.
 * <p/>
 * 
 * The class provides means to login and logout, lookup EJBs within the current
 * context.
 * <p/>
 * 
 * The manager supports one shadow context. For instance, there is a context
 * that the user opened. It's called the primary context. All lookups are done
 * with the primary context. If the user logs in one more time, the current
 * primary context becomes the shadow context, which is kept open but is not
 * used for lookups. The context opened during the second login becomes the new
 * primary context. Then, if the user logs out, the primary context is closed
 * and the shadow context becomes the primary. As it was already mentioned the
 * manager supports only one shadow context, which means that the user log in at
 * most 2 times without logging out.
 * 
 * @author Danylo Vashchilenko
 */
public class ContextManager extends Observable {

	static {
		instance = new ContextManager();
	}

	protected ContextManager() {
		programmaticLogin = new ProgrammaticLogin();
	}

	public void login(String username, String password)
			throws ValidationException {

		Logger logger = Logger.getLogger(ContextManager.class);

		logger.debug("Log in attempt: " + username);

		try {
			programmaticLogin.login(username, hashWithSHA256(password)
					.toCharArray());
		} catch (Exception e) {
			throw new RuntimeException("Failed to login", e);
		}

		InitialContext newContext;
		try {
			newContext = new InitialContext();
		} catch (NamingException ex) {
			throw new RuntimeException("Failed to create InitialContext", ex);
		}

		try {
			doLookup(AdministratorsServiceRemote.class, newContext).ping();
		} catch (Exception ex) {
			Logger.getLogger(ContextManager.class).info(
					"Authentication failed for the user: " + username, ex);
			throw new ValidationException(ResourcesManager.getStrings()
					.getString("Message.LoggingInFailed"));
		}

		if (context == null) {
			context = newContext;
		} else if (shadowContext == null) {
			shadowContext = context;
			context = newContext;
		} else {
			throw new IllegalStateException(
					"There is already a primary and a shadow contexts.");
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
			md = MessageDigest.getInstance("SHA-256"); // NOI18N
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		try {
			md.update(String.valueOf(password).getBytes("UTF-8")); // NOI18N
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}

		byte byteData[] = md.digest();

		/*
		 * Converts the bytes to a hex string
		 */
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}

	public void logout() {
		try {
			context.close();
		} catch (NamingException ex) {
			Logger.getLogger(ContextManager.class).error(
					"Failed to close the context!", ex);
		}

		if (shadowContext != null) {
			context = shadowContext;
			shadowContext = null;
		} else {
			context = null;
		}

		setChanged();
		notifyObservers(context);
	}

	/**
	 * Looks up the EJB using the current context.
	 * 
	 * @param <T>
	 *            the type to lookup
	 * @param clazz
	 *            the class of the type to lookup
	 * @return an instance of the EJB
	 */
	public static <T> T lookup(Class<T> clazz) {
		return instance.doLookup(clazz, instance.context);
	}

	public static <T> T lookup(String jndiName) {
		return instance.doLookup(jndiName);
	}

	protected <T> T doLookup(Class<T> clazz, InitialContext context) {

		/*
		 * If no context is available (e.g. no current user), throws a runtime
		 * exception.
		 */
		if (context == null) {
			throw new IllegalStateException("No context is available");
		}

		try {
			Logger.getLogger(ContextManager.class).debug(
					"Looking up: " + clazz.getSimpleName());
			
			// TODO: add jndi name caching
			return (T) context.lookup("java:global/key2gym/business/"
					+ clazz.getSimpleName().replaceAll("Remote", "Bean") + "!"
					+ clazz.getName());
		} catch (NamingException ex) {
			throw new RuntimeException("Failed to lookup an EJB: ", ex);
		} catch (ClassCastException ex) {
			throw new RuntimeException("");
		}
	}

	protected <T> T doLookup(String jndiName) {

		/*
		 * If no context is available (e.g. no current user), throws a runtime
		 * exception.
		 */
		if (context == null) {
			throw new IllegalStateException("No context is available");
		}

		try {
			return (T) instance.context.lookup(jndiName);
		} catch (NamingException ex) {
			throw new RuntimeException("Resource not available", ex);
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
	private InitialContext context;
	/**
	 * The shadow context.
	 * 
	 * The user can open a second session without closing the first one. The
	 * primary context becomes the shadow context and the second context becomes
	 * the new primary context.
	 */
	private InitialContext shadowContext;
	/**
	 * Used to perform authentication.
	 */
	private ProgrammaticLogin programmaticLogin;
}
