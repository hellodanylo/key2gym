/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.ValidationException;
import census.persistence.AdSource;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author daniel
 */
public class AdSourcesService extends BusinessService {

    /*
     * Singleton instance.
     */
    private static AdSourcesService instance;

    /**
     * Gets the IDs of all ad sources.
     *
     * @throws IllegalStateException if the session is not active
     * @return the list of IDs of all ad sources
     */
    public List<Short> getAdSourcesIds() {
        assertSessionActive();
        
        List<AdSource> adSources = (List<AdSource>)entityManager
                .createNamedQuery("AdSource.findAll") //NOI18N
                .getResultList(); 
        List<Short> result = new LinkedList<>();

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
    public List<AdSource> getAdSources() {
        assertSessionActive();
        
        List<AdSource> adSources = entityManager.createNamedQuery("AdSource.findAll").getResultList(); //NOI18N

        for (AdSource adSource : adSources) {
            entityManager.detach(adSource);
        }

        return adSources;
    }

    /**
     * Finds the ad source by its ID.
     * @param adSourceId the ad source's ID
     * @throws ValidationException if the ad source's ID is null or invalid
     * @throws IllegalStateException if the session is not active
     * @return the ad source
     */
    public AdSource findAdSourceById(Short adSourceId) throws ValidationException {
        assertSessionActive();
        
        if(adSourceId == null)
            throw new NullPointerException("The ad source's ID is null."); // NOI18N
        
        return entityManager.find(AdSource.class, adSourceId);
    }

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
