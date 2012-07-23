/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business;

import census.business.dto.TimeRangeDTO;
import census.persistence.TimeRange;
import java.util.LinkedList;
import java.util.List;
import org.joda.time.LocalTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TimeRangesService extends BusinessService {
    
    /**
     * Singleton instance
     */
    private static TimeRangesService instance;

    /**
     * Gets all time ranges
     * 
     * @throws IllegalStateException if the session is not active
     * @return a list of time ranges
     */
    public List<TimeRangeDTO> getAllTimeRanges() {
        assertSessionActive();
        
        List<TimeRangeDTO> result = new LinkedList<TimeRangeDTO>();
        List<TimeRange> timeRanges = entityManager
                .createNamedQuery("TimeRange.findAll")
                .getResultList();
        
        for(TimeRange timeRange : timeRanges) {
            TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
            timeRangeDTO.setId(timeRange.getId());
            timeRangeDTO.setBegin(new LocalTime(timeRange.getTimeBegin()));
            timeRangeDTO.setEnd(new LocalTime(timeRange.getTimeEnd()));
            
            result.add(timeRangeDTO);
        }
        
        return result;
    }
    
    public static TimeRangesService getInstance() {
        if(instance == null) {
            instance = new TimeRangesService();
        }
        return instance;
    }
}
