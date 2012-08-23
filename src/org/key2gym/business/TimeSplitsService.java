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

import org.key2gym.business.dto.TimeSplitDTO;
import org.key2gym.persistence.TimeSplit;
import java.util.LinkedList;
import java.util.List;
import org.joda.time.LocalTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TimeSplitsService extends BusinessService {
    
    /**
     * Gets all time ranges.
     * 
     * @throws IllegalStateException if the session is not active
     * @return a list of time ranges
     */
    public List<TimeSplitDTO> getAll() {
        assertOpenSessionExists();
        
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
    
    /**
     * Singleton instance.
     */
    private static TimeSplitsService instance;
    
    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static TimeSplitsService getInstance() {
        if(instance == null) {
            instance = new TimeSplitsService();
        }
        return instance;
    }
}
