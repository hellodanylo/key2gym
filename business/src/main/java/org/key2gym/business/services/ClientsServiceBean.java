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

import org.joda.time.DateMidnight;
import org.key2gym.business.api.*;
import org.key2gym.business.api.dtos.ClientDTO;
import org.key2gym.business.api.dtos.Debtor;
import org.key2gym.business.api.services.ClientsService;
import org.key2gym.business.entities.Client;
import org.key2gym.business.resources.ResourcesManager;
import org.springframework.stereotype.Service;
import scala.math.BigDecimal;
import scala.math.BigDecimal$;

import javax.annotation.security.RolesAllowed;
import javax.persistence.NoResultException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.ClientsService")
@RolesAllowed({ SecurityRoles.JUNIOR_ADMINISTRATOR,
	SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER })
public class ClientsServiceBean extends BasicBean implements ClientsService {

    @Override
    public ClientDTO getTemplateClient() {
        ClientDTO client = new ClientDTO();

        client.setId(getNextId());
        client.setAttendancesBalance(0);
        client.setExpirationDate(new DateMidnight());
        client.setMoneyBalance(BigDecimal$.MODULE$.apply(0).setScale(2).underlying());
        client.setRegistrationDate(new DateMidnight());

        return client;
    }

    @Override
    public Integer registerClient(ClientDTO client, Boolean useSecuredProperties) throws BusinessException, ValidationException, SecurityViolationException {
        
        if (client == null) {
            throw new NullPointerException("The client is null."); //NOI18N
        }

        if (useSecuredProperties == null) {
            throw new NullPointerException("The useSecuredPropeties is null."); //NOI18N
        }

        Client newClient = new Client();

        /*
         * Validation
         */
        newClient.setFullName(client.getFullName());

        getCardValidator().validate(client.getCard());
        newClient.setCard(client.getCard());

        newClient.setNote(client.getNote());

        newClient.setRegistrationDate(new Date());

        if (useSecuredProperties && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Client.UpdateSecuredProperties.Denied"));
        }

        if (useSecuredProperties) {
            newClient.setAttendancesBalance(client.getAttendancesBalance());
            newClient.setMoneyBalance(new BigDecimal(client.getMoneyBalance()));
            newClient.setExpirationDate(client.getExpirationDate().toDate());
        } else {
            newClient.setAttendancesBalance(0);
            newClient.setMoneyBalance(BigDecimal$.MODULE$.apply(0));
            newClient.setExpirationDate(client.getRegistrationDate().toDate());
        }


        em.persist(newClient);
        em.flush();

        return newClient.getId();
    }

    @Override
    public ClientDTO getById(Integer clientId) throws ValidationException {


        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        Client client = em.find(Client.class, clientId);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientId);
        clientDTO.setFullName(client.getFullName());
        clientDTO.setAttendancesBalance(client.getAttendancesBalance());
        clientDTO.setMoneyBalance(client.getMoneyBalance().underlying());
        clientDTO.setRegistrationDate(new DateMidnight(client.getRegistrationDate()));
        clientDTO.setExpirationDate(new DateMidnight(client.getExpirationDate()));
        clientDTO.setNote(client.getNote());
        clientDTO.setCard(client.getCard());

        return clientDTO;
    }

    @Override
    public Integer findByCard(Integer card) {


        if (card == null) {
            throw new NullPointerException("The card is null."); //NOI18N
        }

        try {
            Client client = (Client) em.createNamedQuery("Client.findByCard") //NOI18N
                    .setParameter("card", card) //NOI18N
                    .setMaxResults(1)
                    .getSingleResult();
            return client.getId();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<ClientDTO> findByFullName(String fullName, Boolean exactMatch) throws IllegalArgumentException {


        if (fullName == null) {
            throw new IllegalArgumentException("The fullName is null."); //NOI18N
        }

        if (exactMatch == null) {
            throw new IllegalArgumentException("The exactMatch is null."); //NOI18N
        }

        List<Client> clients;

        if (exactMatch == true) {
            clients = em.createNamedQuery("Client.findByFullNameExact") //NOI18N
                    .setParameter("fullName", fullName) //NOI18N
                    .getResultList();
        } else {
            clients = em.createNamedQuery("Client.findByFullNameNotExact") //NOI18N
                    .setParameter("fullName", fullName) //NOI18N
                    .getResultList(); //NOI18N
        }

        List<ClientDTO> result = new LinkedList<ClientDTO>();

        for (Client client : clients) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(client.getId());
            clientDTO.setFullName(client.getFullName());
            clientDTO.setAttendancesBalance(client.getAttendancesBalance());
            clientDTO.setMoneyBalance(client.getMoneyBalance().underlying());
            clientDTO.setRegistrationDate(new DateMidnight(client.getRegistrationDate()));
            clientDTO.setExpirationDate(new DateMidnight(client.getExpirationDate()));
            clientDTO.setNote(client.getNote());
            clientDTO.setCard(client.getCard());
            result.add(clientDTO);
        }

        return result;
    }

    @Override
    public void updateClient(ClientDTO client, Boolean useSecuredProperties) throws SecurityViolationException, ValidationException {
        


        if (client == null) {
            throw new NullPointerException("The client is null."); //NOI18N
        }

        if (client.getId() == null) {
            throw new NullPointerException("The client's ID is null."); //NOI18N
        }

        if (useSecuredProperties == null) {
            throw new NullPointerException("The useSecuredProperties is null."); //NOI18N
        }

        Client originalClient = em.find(Client.class, client.getId());

        if (originalClient == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        if (useSecuredProperties && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Client.UpdateSecuredProperties.Denied"));
        }

        originalClient.setFullName(client.getFullName());

        /*
         * Validates the card, only if it was changed. Otherwise, the validator
         * will throw ValidationException for the card is already assigned to
         * this client.
         */
        if ((originalClient.getCard() != null && !originalClient.getCard().equals(client.getCard()))
                || (client.getCard() != null && !client.getCard().equals(originalClient.getCard()))) {
            getCardValidator().validate(client.getCard());
            originalClient.setCard(client.getCard());
        }

        originalClient.setNote(client.getNote());
        originalClient.setRegistrationDate(client.getRegistrationDate().toDate());

	if(useSecuredProperties) {
	    originalClient.setAttendancesBalance(client.getAttendancesBalance());
	    originalClient.setMoneyBalance(new BigDecimal(client.getMoneyBalance()));
	    originalClient.setExpirationDate(client.getExpirationDate().toDate());
	}

    }

    @Override
    public List<Debtor> findDebtors() throws SecurityViolationException {

        if(!callerHasAnyRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(ResourcesManager.getString("Security.Access.Denied"));
        }

        List<org.key2gym.business.entities.Debtor> debtors =
                em.createNamedQuery("Debtor.findAll", org.key2gym.business.entities.Debtor.class)
                .getResultList();

        List<Debtor> result = new ArrayList<>(debtors.size());

        for(org.key2gym.business.entities.Debtor debtor: debtors) {
            Debtor resultDebtor = new Debtor();
            resultDebtor.setClientId(debtor.getClientId());
            resultDebtor.setClientFullName(debtor.getFullName());
            resultDebtor.setMoneyBalance(debtor.getMoneyBalance());
            resultDebtor.setLastAttendance(debtor.getLastAttendance());
            result.add(resultDebtor);
        }

        return result;
    }

    @Override
    public Boolean hasDebt(Integer clientId) throws ValidationException {

        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        return client.getMoneyBalance().compare(BigDecimal$.MODULE$.apply(0)) < 0;
    }

    @Override
    public Integer getNextId() {

        try {
            return 1 + (Integer) em
                    .createNamedQuery("Client.findAllIdsOrderByIdDesc") //NOI18N
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 1;
        }
    }

    public Validator getCardValidator() {
        return new Validator<Integer>() {
            @Override
            public void validate(Integer value) throws ValidationException {
                // If a card is to be assigned,
                // let's make sure no one else uses it.
                if (value != null) {
                    List<Client> clients = em
                            .createNamedQuery("Client.findByCard") //NOI18N
                            .setParameter("card", value) //NOI18N
                            .getResultList();

                    if (!clients.isEmpty()) {
                        String message = MessageFormat.format(
                                getString("Invalid.Client.Card.AlreadyInUse.withClientFullNameAndID"),
                                new Object[]{
                                    clients.get(0).getFullName(),
                                    clients.get(0).getId()});
                        throw new ValidationException(message);
		    }
                }
            }
        };
    }
}
