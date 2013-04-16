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


import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityRoles;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.FreezeDTO;
import org.key2gym.business.api.services.AdministratorsService;
import org.key2gym.business.api.services.AttendancesService;
import org.key2gym.business.api.services.FreezesService;
import org.key2gym.business.entities.Client;
import org.key2gym.persistence.Administrator;
import org.key2gym.persistence.ClientFreeze;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 *
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.FreezesService")
@Secured({ SecurityRoles.JUNIOR_ADMINISTRATOR,
	SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER })
public class FreezesServiceBean extends BasicBean implements FreezesService {

    @Override
    public void addFreeze(Integer clientId, Integer days, String note) throws ValidationException, BusinessException, SecurityViolationException {

        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        if (days == null) {
            throw new NullPointerException("The days is null."); //NOI18N
        }

        if (note == null) {
            throw new NullPointerException("The note is null."); //NOI18N
        }

        if (!callerHasAnyRole(SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        Client client = getEntityManager().find(Client.class, clientId);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        if (client.getExpirationDate().compareTo(new Date()) < 0) {
            throw new BusinessException(getString("BusinessRule.Client.SubscriptionExpired"));
        }

        if (days < 1 || days > 10) {
            String message = MessageFormat.format(
                    getString("BusinessRule.Freeze.Days.HasToBeWithinRange.withRangeBeginAndRangeEnd"),
                    1, 10
            );
            throw new ValidationException(message);
        }

        if (note.trim().isEmpty()) {
            throw new ValidationException(getString("Invalid.Freeze.Note.CanNotBeEmpty"));
        }

        DateMidnight today = new DateMidnight();
        List<ClientFreeze> freezes = getEntityManager()
                .createNamedQuery("ClientFreeze.findByClientAndDateIssuedRange") //NOI18N
                .setParameter("client", client)
                .setParameter("rangeBegin", today.minusMonths(1).toDate()) //NOI18N
                .setParameter("rangeEnd", today.toDate()) //NOI18N
                .getResultList();

        if (!freezes.isEmpty()) {
            throw new BusinessException(getString("BusinessRule.Freeze.ClientHasAlreadyBeenFrozenLastMonth"));
        }

        // Rolls the expiration date
        client.setExpirationDate(new DateMidnight(client.getExpirationDate()).plusDays(days).toDate());

        ClientFreeze clientFreeze = new ClientFreeze();
        clientFreeze.setDateIssued(new Date());
        clientFreeze.setDays(days);
        clientFreeze.setClient(client);
        
        clientFreeze.setAdministrator(getEntityManager().find(Administrator.class, 
        		administratorsService.getByUsername(getCallerPrincipal()).getId()));
        clientFreeze.setNote(note);

        getEntityManager().persist(clientFreeze);
        getEntityManager().flush();
    }

    @Override
    public List<FreezeDTO> findFreezesForClient(Integer clientId) throws ValidationException {

        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        Client client = getEntityManager().find(Client.class, clientId);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        List<ClientFreeze> freezes = getEntityManager().createNamedQuery("ClientFreeze.findByClient").setParameter("client", client).getResultList(); //NOI18N
        List<FreezeDTO> result = new LinkedList<FreezeDTO>();

        for (ClientFreeze freeze : freezes) {
            result.add(wrapFreeze(freeze));
        }

        return result;
    }

    @Override
    public List<FreezeDTO> findByDateIssuedRange(DateMidnight begin, DateMidnight end) throws SecurityViolationException, ValidationException {
        
        if(!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }
        
        if(begin == null) {
            throw new NullPointerException("The begin is null."); //NOI18N
        }
        
        if(end == null) {
            throw new NullPointerException("The end is null."); //NOI18N
        }
        
        if(begin.isAfter(end)) {
            throw new ValidationException(getString("Invalid.DateRange.BeginningAfterEnding"));
        }
        
        List<ClientFreeze> freezes = getEntityManager()
                .createNamedQuery("ClientFreeze.findByDateIssuedRange") //NOI18N
                .setParameter("rangeBegin", begin.toDate()) //NOI18N
                .setParameter("rangeEnd", end.toDate()) //NOI18N
                .getResultList();
        List<FreezeDTO> result = new LinkedList<FreezeDTO>();
        
        for(ClientFreeze freeze : freezes) {
            result.add(wrapFreeze(freeze));
        }
        
        return result;
    }

    @Override
    public List<FreezeDTO> findAll() {
        
        List<ClientFreeze> freezes = getEntityManager()
                .createNamedQuery("ClientFreeze.findAll") //NOI18N
                .getResultList();
        List<FreezeDTO> result = new LinkedList<FreezeDTO>();
        
        for(ClientFreeze freeze : freezes) {
            result.add(wrapFreeze(freeze));
        }
        
        return result;
    }
    
    @Override
    public void remove(Integer id) throws SecurityViolationException, ValidationException, BusinessException {

        if(!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }
        
        if(id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }
        
        ClientFreeze clientFreeze = getEntityManager().find(ClientFreeze.class, id);
        
        if(clientFreeze == null) {
            throw new ValidationException(getString("Invalid.Freeze.ID"));
        }
        
  
        if(new DateMidnight(clientFreeze.getDateIssued()).plusDays(clientFreeze.getDays()).isBeforeNow()) {
            throw new BusinessException(getString("BusinessRule.Freeze.AlreadyExpired"));
        }
        
        Client client = clientFreeze.getClient();
        
        client.setExpirationDate(new DateMidnight(client.getExpirationDate()).minusDays(clientFreeze.getDays()).toDate());
        
        // TODO: note change
        getEntityManager().remove(clientFreeze);
        getEntityManager().flush();
    }
    

    public FreezeDTO wrapFreeze(ClientFreeze freeze) {
        FreezeDTO freezeDTO = new FreezeDTO();

        freezeDTO.setId(freeze.getId());
        freezeDTO.setAdministratorFullName(freeze.getAdministrator().getFullName());
        freezeDTO.setAdministratorId(freeze.getAdministrator().getId());
        freezeDTO.setClientFullName(freeze.getClient().getFullName());
        freezeDTO.setClientId(freeze.getClient().getId());
        freezeDTO.setDateIssued(new DateMidnight(freeze.getDateIssued()));
        freezeDTO.setDays(freeze.getDays());
        freezeDTO.setNote(freeze.getNote());
        
        return freezeDTO;
    }
    
    @Autowired
    private AdministratorsService administratorsService;
}
