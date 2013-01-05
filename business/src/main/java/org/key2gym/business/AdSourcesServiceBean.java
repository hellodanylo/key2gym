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
package org.key2gym.business;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.key2gym.business.api.*;
import org.key2gym.business.api.dtos.AdSourceDTO;
import org.key2gym.business.api.remote.AdSourcesServiceRemote;
import org.key2gym.persistence.AdSource;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(AdSourcesServiceRemote.class)
public class AdSourcesServiceBean extends BasicBean implements AdSourcesServiceRemote {

    @Override
    public List<Integer> getAdSourcesIds() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<AdSource> adSources = (List<AdSource>) entityManager.createNamedQuery("AdSource.findAll") //NOI18N
                .getResultList();
        List<Integer> result = new LinkedList<Integer>();

        for (AdSource adSource : adSources) {
            result.add(adSource.getId());
        }

        return result;
    }

    @Override
    public List<AdSourceDTO> getAdSources() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<AdSource> adSources = entityManager.createNamedQuery("AdSource.findAll").getResultList(); //NOI18N
        List<AdSourceDTO> adSourceDTOs = new LinkedList<AdSourceDTO>();

        for (AdSource adSource : adSources) {
            adSourceDTOs.add(wrapAdSource(adSource));
        }

        return adSourceDTOs;
    }

    @Override
    public AdSourceDTO findAdSourceById(Integer adSourceId) throws ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        if (adSourceId == null) {
            throw new NullPointerException("The ad source's ID is null."); // NOI18N
        }

        AdSource adSource = entityManager.find(AdSource.class, adSourceId);

        if (adSource == null) {
            throw new ValidationException(getString("Invalid.AdSource.ID"));
        }

        return wrapAdSource(adSource);
    }

    public AdSourceDTO wrapAdSource(AdSource adSource) {
        AdSourceDTO dto = new AdSourceDTO();

        dto.setId(adSource.getId());
        dto.setTitle(adSource.getTitle());

        return dto;
    }
    @PersistenceContext(unitName = "PU")
    private EntityManager entityManager;
}
