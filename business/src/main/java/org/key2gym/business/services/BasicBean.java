package org.key2gym.business.services;

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

import javax.persistence.EntityManager;

import org.key2gym.business.api.services.BasicService;
import org.key2gym.business.resources.ResourcesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Danylo Vashchilenko
 */
public class BasicBean implements BasicService {

	static {
		org.apache.log4j.PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
	}

	protected boolean callerHasRole(String role) {
		return SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication()
						.getAuthorities()
						.contains(new SimpleGrantedAuthority(role));
	}

	protected boolean callerHasAnyRole(String... roles) {
		for (String role : roles) {
			if (callerHasRole(role)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	protected String getString(String key, String... arguments) {
		return ResourcesManager.getString(key, arguments);
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	protected String getCallerPrincipal() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@Autowired
	@Qualifier("entityManager")
	protected EntityManager em;
}
