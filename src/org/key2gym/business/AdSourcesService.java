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
package org.key2gym.business;

import org.key2gym.business.api.ValidationException;
import org.key2gym.persistence.AdSource;
import java.util.LinkedList;
import java.util.List;
import org.key2gym.business.dto.AdSourceDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AdSourcesService extends BusinessService {

    /**
     * Gets the IDs of all ad sources.
     *
     * @throws IllegalStateException if the session is not active
     * @return the list of IDs of all ad sources
     */
    public List<Integer> getAdSourcesIds() {
        assertOpenSessionExists();

        List<AdSource> adSources = (List<AdSource>) entityManager.createNamedQuery("AdSource.findAll") //NOI18N
                .getResultList();
        List<Integer> result = new LinkedList<>();

        for (AdSource adSource : adSources) {
            result.add(adSource.getId());
        }

        return result;
    }

    /**
     * Gets all ad sources.
     *
     * @throws IllegalStateException if the session is not active
     * @return the list of all ad sources.
     */
    public List<AdSourceDTO> getAdSources() {
        assertOpenSessionExists();

        List<AdSource> adSources = entityManager.createNamedQuery("AdSource.findAll").getResultList(); //NOI18N
        List<AdSourceDTO> adSourceDTOs = new LinkedList<>();
        
        for (AdSource adSource : adSources) {
            adSourceDTOs.add(wrapAdSource(adSource));
        }

        return adSourceDTOs;
    }
    
    AdSourceDTO wrapAdSource(AdSource adSource) {
        AdSourceDTO dto = new AdSourceDTO();
        
        dto.setId(adSource.getId());
        dto.setTitle(adSource.getTitle());
        
        return dto;
    }

    /**
     * Finds the ad source by its ID.
     *
     * @param adSourceId the ad source's ID
     * @throws ValidationException if the ad source's ID is null or invalid
     * @throws IllegalStateException if the session is not active
     * @return the ad source
     */
    public AdSource findAdSourceById(Integer adSourceId) throws ValidationException {
        assertOpenSessionExists();

        if (adSourceId == null) {
            throw new NullPointerException("The ad source's ID is null."); // NOI18N
        }
        return entityManager.find(AdSource.class, adSourceId);
    }
    /**
     * Singleton instance.
     */
    private static AdSourcesService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class.
     */
    public static AdSourcesService getInstance() {
        if (instance == null) {
            instance = new AdSourcesService();
        }
        return instance;
    }
}
