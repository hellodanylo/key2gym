package org.key2gym.business;

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


import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.joda.time.LocalTime;
import org.key2gym.business.api.*;
import org.key2gym.business.api.dtos.TimeSplitDTO;
import org.key2gym.business.api.remote.TimeSplitsServiceRemote;
import org.key2gym.business.entities.TimeSplit;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(TimeSplitsServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
@RolesAllowed({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class TimeSplitsServiceBean extends BasicBean implements TimeSplitsServiceRemote {
    
    @Override
    public List<TimeSplitDTO> getAll() {
        
        List<TimeSplitDTO> result = new LinkedList<TimeSplitDTO>();
        List<TimeSplit> timeSplits = getEntityManager()
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
}
