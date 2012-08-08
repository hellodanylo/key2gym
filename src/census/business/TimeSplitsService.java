/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business;

import census.business.dto.TimeSplitDTO;
import census.persistence.TimeSplit;
import java.util.LinkedList;
import java.util.List;
import org.joda.time.LocalTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TimeSplitsService extends BusinessService {
    
    /**
     * Singleton instance
     */
    private static TimeSplitsService instance;

    /**
     * Gets all time ranges
     * 
     * @throws IllegalStateException if the session is not active
     * @return a list of time ranges
     */
    public List<TimeSplitDTO> getAllTimeRanges() {
        assertSessionActive();
        
        List<TimeSplitDTO> result = new LinkedList<TimeSplitDTO>();
        List<TimeSplit> timeSplits = entityManager
                .createNamedQuery("TimeSplit.findAll")
                .getResultList();
        
        for(TimeSplit timeSplit : timeSplits) {
            TimeSplitDTO timeRangeDTO = new TimeSplitDTO();
            timeRangeDTO.setId(timeSplit.getId());
            timeRangeDTO.setTime(new LocalTime(timeSplit.getTime()));
            timeRangeDTO.setTitle(timeSplit.getTitle());
            
            result.add(timeRangeDTO);
        }
        
        return result;
    }
    
    public static TimeSplitsService getInstance() {
        if(instance == null) {
            instance = new TimeSplitsService();
        }
        return instance;
    }
}
